package com.kr4ken.dp.services.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.config.HabiticaConfig;
import com.kr4ken.dp.config.TrelloConfig;
import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.HabiticaService;
import com.kr4ken.dp.services.intf.TrelloService;
import com.kr4ken.habitica.Habitica;
import com.kr4ken.habitica.domain.DailyRepeat;
import com.kr4ken.habitica.domain.Tag;
import com.kr4ken.habitica.exception.HabiticaHttpException;
import com.kr4ken.habitica.impl.HabiticaImpl;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableIndexAnyGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
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

    @Override
    public Task saveTask(Task task) {
        // Проверка наличия задачи в Хабитике
        Boolean update;
        com.kr4ken.habitica.domain.Task result;
        try {
            result = habiticaApi.getTask(task.getTrelloId());
            update = true;
        } catch (HabiticaHttpException e) {
            result = new com.kr4ken.habitica.domain.Task();
            update = false;
        }
        // Алиас
        result.setAlias(task.getTrelloId());
        // Наименование задачи в habitica
        String text = "";
        if (task.getDuration() != null)
            text += "{" + task.getDuration().toString() + "} ";
        String subtaskName = null;
        Optional<TaskCheckList> currentCheckList = null;
        Integer currentCheckListCount = 0;
        Integer currentCheckedCount = 0;
        if (task.getChecklists() != null) {
            currentCheckList = task.getChecklists()
                    .stream()
                    .filter(e -> e.getChecklistItems() != null)
                    .filter(e -> e.getChecklistItems()
                            .stream()
                            .anyMatch(taskCheckListItem -> !taskCheckListItem.getChecked()))
                    .findAny();
            if (currentCheckList.isPresent()) {
                for (TaskCheckListItem item : currentCheckList.get().getChecklistItems().stream().sorted(Comparator.comparing(TaskCheckListItem::getPos)).collect(Collectors.toList())) {
                    if (item.getChecked())
                        currentCheckedCount++;
                    else
                        subtaskName = subtaskName == null ? item.getName() : subtaskName;
                }
                currentCheckListCount = currentCheckList.get().getChecklistItems().size();
            }
        }
        if (subtaskName != null)
            text += subtaskName + " [" + task.getName() + "]";
        else
            text += task.getName();
        result.setText(text);

        // Атрибут
        result.setAttribute(task.getAttribute().toString().toLowerCase());
        // Сложность
        result.setPriority(1.);
        // Начальное значение
        result.setValue(0.);
        // Теги
        List<String> tagNames = new ArrayList<>();
        if (task.getUrgent())
            tagNames.add("Срочно");
        if (task.getImportant())
            tagNames.add("Важно");
        if (trelloConfig.getIdNameMap().get(task.getType().getTrelloId()) != null)
            tagNames.add(task.getType().getName());
        result.setTags(mergeTags(tagNames));

        // Пояснение задачи
        String notes = "";
        if (currentCheckList != null && currentCheckList.isPresent()) {
            if (task.getDescription() != null)
                notes += "![" + task.getDescription()+  "](http://progressed.io/bar/" + currentCheckedCount.toString() +"?scale="+currentCheckListCount+"&suffix=+)";
            else
                notes += "![](http://progressed.io/bar/" + currentCheckedCount.toString() +"?scale="+currentCheckListCount+"&suffix=+)";
        }
        else
        if (task.getDescription() != null)
            notes += task.getDescription();
        result.setNotes(notes);

        // Тип
        if (task.getType().getTrelloId().equals(trelloConfig.getHabitTaskList())) {
            result.setType("habit");
        } else {
            if (task.getType().getTrelloId().equals(trelloConfig.getDailyTaskList())) {
                result.setType("habit");
            } else {
                result.setType("todo");
            }
        }
//        result.setType(trelloConfig.getIdNameMap().get(task.getType().getTrelloId()) == null?trelloConfig.getIdNameMap().get(task.getType().getTrelloId()):"todo");
        // Выполнена ли задача
        result.setCompleted(false);
        //Настройка повтора для daily
        DailyRepeat repeat = new DailyRepeat();
        result.setRepeat(repeat);
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
                    newTag = true;
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

