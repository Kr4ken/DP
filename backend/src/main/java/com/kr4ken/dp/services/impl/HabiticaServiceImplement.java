package com.kr4ken.dp.services.impl;

import com.kr4ken.dp.config.HabiticaConfig;
import com.kr4ken.dp.config.TrelloConfig;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskAttribute;
import com.kr4ken.dp.models.entity.TaskCheckList;
import com.kr4ken.dp.models.entity.TaskCheckListItem;
import com.kr4ken.dp.models.repository.TaskRepository;
import com.kr4ken.dp.models.repository.TaskTypeRepository;
import com.kr4ken.dp.services.intf.HabiticaService;
import com.kr4ken.dp.utils.TaskUtils;
import com.kr4ken.habitica.Habitica;
import com.kr4ken.habitica.domain.DailyRepeat;
import com.kr4ken.habitica.domain.Tag;
import com.kr4ken.habitica.exception.HabiticaHttpException;
import com.kr4ken.habitica.impl.HabiticaImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabiticaServiceImplement implements HabiticaService {
    private final HabiticaConfig habiticaConfig;
    private final TrelloConfig trelloConfig;
    private final Habitica habiticaApi;
    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;

    @Autowired
    HabiticaServiceImplement(
            HabiticaConfig habiticaConfig,
            TrelloConfig trelloConfig,
            TaskTypeRepository taskTypeRepository,
            TaskRepository taskRepository
    ) {
        this.habiticaConfig = habiticaConfig;
        this.trelloConfig = trelloConfig;
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        // Подгрузка конфигурации
        habiticaApi = new HabiticaImpl(habiticaConfig.getApiUser(), habiticaConfig.getApiKey());
    }

    @Override
    public List<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (com.kr4ken.habitica.domain.Task task : habiticaApi.getUserTasks()) {
            result.add(new Task(task.getAlias(),
                    task.getText(),
                    task.getNotes(),
                    null,
                    false,
                    false,
                    null,
                    taskTypeRepository.findOne(1L),
                    null,
                    null,
                    TaskAttribute.valueOf(task.getAttribute().replace(task.getAttribute().charAt(0), Character.toUpperCase(task.getAttribute().charAt(0)))),
                    1.
            ));
        }
        return result;
    }

    @Override
    public List<Task> getTrelloTasks() {
        return habiticaApi.getUserTasks()
                .stream()
                .map(e -> {
                    Optional<Task> task = taskRepository.findByTrelloId(e.getAlias());
                    return task.isPresent() ? task.get() : null;
                })
                .collect(Collectors.toList());
    }

    String getTaskType(Task task) {
        // Тип
        if (task.getType().getTrelloId().equals(trelloConfig.getHabitTaskList()))
            return "habit";
        if (task.getType().getTrelloId().equals(trelloConfig.getDailyTaskList()))
            return "daily";
        return "todo";
    }

    @Override
    public Task saveTask(Task task) {
        // Проверка наличия задачи в Хабитике
        Boolean update;
        com.kr4ken.habitica.domain.Task result;
        try {
            result = habiticaApi.getTask(task.getTrelloId());
            if (result.getType().equals(getTaskType(task)))
                update = true;
                // Нет возможности изменить тип в хабитике, поэтому удаляю и заново создаю задачу
            else {
                habiticaApi.deleteTask(result.getId());
                result = new com.kr4ken.habitica.domain.Task();
                update = false;
            }
        } catch (HabiticaHttpException e) {
            result = new com.kr4ken.habitica.domain.Task();
            update = false;
        }
        // Алиас
        result.setAlias(task.getTrelloId());
        // Наименование задачи в habitica
        String text = "";
        String subtaskName = null;
        Optional<TaskCheckList> currentCheckList = null;
        TaskCheckListItem subTask = null;
        Integer currentCheckListCount = 0;
        Integer currentCheckedCount = 0;
        subTask = TaskUtils.getCurrentSubtask(task);
        if(subTask!= null) {
            subtaskName = subTask.getName();
            currentCheckedCount = Math.toIntExact(TaskUtils.getCheckedCountSubtasks(task));
            currentCheckListCount = Math.toIntExact(TaskUtils.getCountSubtasks(task));
        }

        if (subtaskName != null) {
            text += "{" + subTask.getDuration().toString() + "} ";
            text += subtaskName + " [" + task.getName() + "]";
        } else {
            text += "{" + task.getDuration().toString() + "} ";
            text += task.getName();
        }
        result.setText(text);

        // Атрибут
        result.setAttribute(task.getAttribute().toString().toLowerCase());
        //Если создаем новую задачу, то устанавливаем начальные значения
        if (!update) {
            // Сложность
            result.setPriority(1.);
            // Начальное значение
            result.setValue(0.);
            // Выполнена ли задача
            result.setCompleted(false);
            // Периодичность
            DailyRepeat repeat = new DailyRepeat();
            result.setRepeat(repeat);
        }
        // Теги
        List<String> tagNames = new ArrayList<>();
        if (task.getUrgent())
            tagNames.add("Срочно");
        if (task.getImportant())
            tagNames.add("Важно");
        if (trelloConfig.getIdNameMap().get(task.getType().getTrelloId()) == null)
            tagNames.add(task.getType().getName());

        result.setTags(mergeTags(tagNames));

        // Пояснение задачи
        String notes = "";
        // if (currentCheckList != null && currentCheckList.isPresent()) {
        if (currentCheckListCount>0) {
            if (task.getDescription() != null)
                notes += "![" + task.getDescription() + "](http://progressed.io/bar/" + currentCheckedCount.toString() + "?scale=" + currentCheckListCount + "&suffix=+)";
            else
                notes += "![](http://progressed.io/bar/" + currentCheckedCount.toString() + "?scale=" + currentCheckListCount + "&suffix=+)";
        } else if (task.getDescription() != null)
            notes += task.getDescription();
        result.setNotes(notes);

        // Тип
        result.setType(getTaskType(task));
        if(result.getType().equals("todo"))
            result.setCompleted(false);
        //Настройка повтора для daily
        result.setFrequency("daily");
        result.setDate(task.getDueDate());

        if (update)
            habiticaApi.updateTask(result);
        else
            habiticaApi.createTask(result);

        return task;
    }

    private List<String> mergeTags(List<String> tagNames) {
        List<String> tagId = new ArrayList<>();
        List<Tag> tags = habiticaApi.getUserTags();
        for (String name : tagNames) {
            Boolean newTag = true;
            for (Tag tag : tags) {
                if (tag.getName().equals(name)) {
                    tagId.add(tag.getId());
                    newTag = false;
                }
            }
            if (newTag) {
                Tag tag = new Tag();
                tag.setName(name);
                tagId.add(habiticaApi.createTag(tag).getId());
            }
        }
        return tagId;
    }

}

