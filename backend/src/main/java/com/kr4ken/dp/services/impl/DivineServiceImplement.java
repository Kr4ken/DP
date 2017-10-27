package com.kr4ken.dp.services.impl;

import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.DivineService;
import com.kr4ken.dp.services.intf.HabiticaService;
import com.kr4ken.dp.services.intf.TrelloService;
import com.kr4ken.dp.utils.TaskUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DivineServiceImplement implements DivineService {

    private final HabiticaService habiticaService;
    private final TrelloService trelloService;

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final TaskCheckListRepository taskCheckListRepository;
    private final TaskCheckListItemRepository taskCheckListItemRepository;
    private final TaskSpecialRepository taskSpecialRepository;

    @Autowired
    DivineServiceImplement(
            HabiticaService habiticaService,
            TrelloService trelloService,
            TaskTypeRepository taskTypeRepository,
            TaskRepository taskRepository,
            TaskCheckListRepository taskCheckListRepository,
            TaskCheckListItemRepository taskCheckListItemRepository,
            TaskSpecialRepository taskSpecialRepository
    ) {
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        this.trelloService = trelloService;
        this.habiticaService = habiticaService;
        this.taskCheckListRepository = taskCheckListRepository;
        this.taskCheckListItemRepository = taskCheckListItemRepository;
        this.taskSpecialRepository = taskSpecialRepository;
    }

    @Override
    public void importTaskTypesFromTrello() {
        trelloService.getTaskTypes()
                .forEach(e -> {
                    Optional<TaskType> current = taskTypeRepository.findByTrelloId(e.getTrelloId());
                    if (current.isPresent()) {
                        current.get().update(e);
                        taskTypeRepository.save(current.get());
                    } else {
                        taskTypeRepository.save(e);
                    }
                });
    }

    private void mergeTask(Task task) {
        Optional<Task> current = taskRepository.findByTrelloId(task.getTrelloId());
        // Проверяем есть ли уже текущий элемент в моей базе
        // Если есть то просто обновляем текущее значение
        if (task.getTrelloId() != null && current.isPresent()) {
            current.get().update(task);
            taskRepository.save(current.get());
        }
        // Если нет то просто сохраняем
        else {
            taskRepository.save(task);
        }
    }

    @Override
    public void importTasksFromTrello() {
        trelloService.getTasks()
                .forEach(this::mergeTask);

    }

    @Override
    public void importTaskFromTrello(Long id) {
        Task task = taskRepository.findOne(id);
        Task newTask = trelloService.getTask(task);
        mergeTask(newTask);
//        task.update(trelloService.getTask(task));
//        taskRepository.save(task);
    }

    @Override
    public void importTaskTypeFromTrello(Long id) {
        TaskType taskType = taskTypeRepository.findOne(id);
        taskType.update(trelloService.getTaskType(taskType));
        taskTypeRepository.save(taskType);
    }

    @Override
    public void TasksTrelloToHabitica() {
        importTaskTypesFromTrello();
        importTasksFromTrello();

    }

    @Override
    public void exportTasksToHabitica() {
        for (TaskType taskType : trelloService.getActiveList()) {
            for (Task task : taskRepository.findByType(taskType)) {
                habiticaService.saveTask(task);
            }
        }
    }

    @Override
    public void scoreTaskFromHabitica(String habiticaId) {
        Optional<Task> task = taskRepository.findByTrelloId(habiticaId);
        if (task.isPresent())
            scoreTaskFromHabitica(task.get());
    }

    @Override
    public void scoreTaskFromHabitica(Task task) {
        // Если выполнил привычку то ничего не делай
        if (task.getType().equals(trelloService.getHabitType()))
            return;
        // Если выполнил каждодневную задачу то если есть подзадачи, то выполни её, если нет ничего не делай
        if (task.getType().equals(trelloService.getDailyType())) {
            TaskCheckListItem subTask = TaskUtils.getCurrentSubtask(task);
            if (subTask != null) {
                subTask.setChecked(true);
                taskRepository.save(trelloService.saveTask(task));
            }
            habiticaService.saveTask(task);
            return;
        }
        // Иначе задача была из списка todo
        TaskCheckListItem subTask = TaskUtils.getCurrentSubtask(task);
        // Если есть подзадача то выполняем её
        if (subTask != null) {
            subTask.setChecked(true);
            taskRepository.save(trelloService.saveTask(task));
            // Иначе выполняем карточку
        } else {
            // Если есть особенности смотрим их
            if (task.getSpecial() != null) {
                switch (task.getSpecial().getComplete()) {
                    case Delete:
                        taskRepository.delete(trelloService.deleteTask(task));
                        break;
                    case Move:
                        task.setType(trelloService.getCompleteType());
                        taskRepository.save(trelloService.saveTask(task));
                        break;
                    case Arhieve:
                        //TODO: Сделать когда нибудь
                        break;
                    default:
                        task.setType(trelloService.getCompleteType());
                        taskRepository.save(trelloService.saveTask(task));
                }
            }
            // Если нет особенностей то переносим в список выполненных
            task.setType(trelloService.getCompleteType());
            taskRepository.save(trelloService.saveTask(task));
        }
        // В конце сохраняем изменения если они были
        habiticaService.saveTask(task);
    }

    @Override
    public void updateFromTrello(Task task) {
        // Смотрим из какого списка идет обновление
        if(!trelloService.getActiveList().contains(task.getType())) {
           return;
        }
        // Если лист рабочий, то обновляем текущую карточку
        importTaskFromTrello(task.getId());

        // В конце сохраняем изменения если они были
        habiticaService.saveTask(task);
    }

    @Override
    public void updateFromTrello(String trelloId) {

    }
}




