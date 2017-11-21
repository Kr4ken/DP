package com.kr4ken.dp.services.impl;

import com.kr4ken.dp.config.DivineConfig;
import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.DivineService;
import com.kr4ken.dp.services.intf.HabiticaService;
import com.kr4ken.dp.services.intf.TrelloService;
import com.kr4ken.dp.utils.TaskUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

@Service
public class DivineServiceImplement implements DivineService {

    private final HabiticaService habiticaService;
    private final TrelloService trelloService;

    private final DivineConfig divineConfig;

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final InterestRepository interestRepository;
    private final InterestTypeRepository interestTypeRepository;

    @Autowired
    DivineServiceImplement(
            HabiticaService habiticaService,
            TrelloService trelloService,
            TaskTypeRepository taskTypeRepository,
            TaskRepository taskRepository,
            InterestRepository interestRepository,
            InterestTypeRepository interestTypeRepository,
            DivineConfig divineConfig

    ) {
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        this.trelloService = trelloService;
        this.habiticaService = habiticaService;
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
        this.divineConfig = divineConfig;
    }

    // Интересы

    // Перемещает интерес в другую группу в самый верх списка
    private void changeType(Interest interest, InterestType interestType) {
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(interestType);
        interest.setOrd(
                interests.toArray(new Interest[]{})[0].getOrd() / 2
        );
        interest.setType(interestType);
    }

    // Ставит в верх списка случайную карточку и тиражирует изменения в трелло (Возможно)
    private Interest mixInterest(Long interestTypeId) {
        Random random = new Random();
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(interestType);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        int max = interestArray.length - 1, min = 0;
        Integer randomInterest = random.nextInt(max - min + 1) + min;
        changeType(interestArray[randomInterest], interestType);
        // Синхронизация с трелло
        if (divineConfig.getTrelloSync())
            interestRepository.save(trelloService.saveInterest(interestArray[randomInterest]));
        else
            interestRepository.save(interestArray[randomInterest]);

        return interestArray[randomInterest];
    }

    @Override
    public Interest mixInterests(Long interestTypeId) {
        return mixInterest(interestTypeId);
    }

    private Interest moveAndMixInterest(Long interestTypeId, InterestType newInterestType) {
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        changeType(interestArray[0], newInterestType);
        // Переносим текущий интерес в список выполнено
        // Синхронизация с трелло
        if (divineConfig.getTrelloSync()) {
            interestRepository.save(trelloService.saveInterest(interestArray[0]));
        } else {
            interestRepository.save(interestArray[0]);
        }
        // Возвращаем новый выбранный элемент
        return mixInterest(interestTypeId);
    }


    @Override
    public Interest completeInterests(Long interestTypeId) {
        return moveAndMixInterest(interestTypeId, trelloService.getInterestCompleteType());
    }

    @Override
    public Interest referInterests(Long interestTypeId) {
        return moveAndMixInterest(interestTypeId, trelloService.getInterestReferType());
    }

    @Override
    public Interest dropInterests(Long interestTypeId) {
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        // Переносим текущий интерес в список выполнено
        // Синхронизация с трелло
        if (divineConfig.getTrelloSync()) {
            interestRepository.delete(trelloService.deleteInterest(interestArray[0]));
        } else {
            interestRepository.delete(interestArray[0]);
        }
        // Возвращаем новый выбранный элемент
        return mixInterest(interestTypeId);
    }


    // Хабитика

    @Override
    public void exportTasksToHabitica() {
        for (TaskType taskType : trelloService.getActiveList()) {
            for (Task task : taskRepository.findByType(taskType)) {
                habiticaService.saveTask(task);
            }
        }
    }

    @Override
    public void exportTaskToHabitica(Long taskId) {
        Task task = taskRepository.findOne(taskId);
        if (task != null)
            exportTaskToHabitica(task);
    }


    private void exportTaskToHabitica(String trelloId) {
        Optional<Task> task = taskRepository.findByTrelloId(trelloId);
        task.ifPresent(this::exportTaskToHabitica);
    }

    public void exportTaskToHabitica(Task task) {
        habiticaService.saveTask(task);
    }


    @Override
    public void scoreTaskFromHabitica(String habiticaId) {
        Optional<Task> task = taskRepository.findByTrelloId(habiticaId);
        task.ifPresent(this::scoreTaskFromHabitica);
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

    // Трелло

    @Override
    public void importInterestTypesFromTrello() {
        trelloService.getInterestTypes()
                .forEach(e -> {
                    Optional<InterestType> current = interestTypeRepository.findByTrelloId(e.getTrelloId());
                    if (current.isPresent()) {
                        current.get().update(e);
                        interestTypeRepository.save(current.get());
                    } else {
                        interestTypeRepository.save(e);
                    }
                });
    }

    @Override
    public void importInterestTypeFromTrello(InterestType interestType) {
        interestType.update(trelloService.getInterestType(interestType));
        interestTypeRepository.save(interestType);
    }

    @Override
    public void importInterestTypeFromTrello(String interestTypeTrelloId) {
        InterestType interestType = new InterestType(interestTypeTrelloId);
        importInterestTypeFromTrello(interestType);
    }

    @Override
    public void importInterestTypeFromTrello(Long interestTypeId) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType != null)
            importInterestTypeFromTrello(interestType);
    }


    @Override
    public void importInterestsFromTrello() {
        trelloService.getInterests()
                .forEach(e -> {
                    Optional<Interest> current = interestRepository.findByTrelloId(e.getTrelloId());
                    if (current.isPresent()) {
                        current.get().update(e);
                        interestRepository.save(current.get());
                    } else {
                        interestRepository.save(e);
                    }
                });
    }

    @Override
    public void importInterestFromTrello(Interest interest) {
        interest.update(trelloService.getInterest(interest));
        interestRepository.save(interest);

    }

    @Override
    public void importInterestFromTrello(String interestTrelloId) {
        Interest interest = new Interest(interestTrelloId);
        importInterestFromTrello(interest);
    }

    @Override
    public void importInterestFromTrello(Long interestId) {
        Interest interest = interestRepository.findOne(interestId);
        if (interest != null)
            importInterestFromTrello(interest);
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

    @Override
    public void importTaskTypeFromTrello(TaskType taskType) {
        taskType.update(trelloService.getTaskType(taskType));
        taskTypeRepository.save(taskType);
    }


    @Override
    public void importTaskTypeFromTrello(Long id) {
        TaskType taskType = taskTypeRepository.findOne(id);
        if (taskType != null)
            importTaskTypeFromTrello(taskType);
    }

    @Override
    public void importTaskTypeFromTrello(String taskTypeTrelloId) {
        TaskType taskType = new TaskType(taskTypeTrelloId);
        importTaskTypeFromTrello(taskType);

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
    public void importTaskFromTrello(Task task) {
        Task newTask = trelloService.getTask(task);
        mergeTask(newTask);
    }


    @Override
    public void importTaskFromTrello(Long id) {
        Task task = taskRepository.findOne(id);
        if (task != null)
            importTaskFromTrello(task);
    }


    @Override
    public void importTaskFromTrello(String taskTrelloId) {
        Task task = new Task();
        task.setTrelloId(taskTrelloId);
        importTaskFromTrello(task);
    }

    @Override
    public void updateFromTrello(String trelloId) {
        importTaskFromTrello(trelloId);
        exportTaskToHabitica(trelloId);
    }

    // Экспорт


    @Override
    public void exportInterestTypesToTrello() {
        interestTypeRepository.findAll()
                .forEach(e -> {
                    e.update(trelloService.saveInterestType(e));
                    interestTypeRepository.save(e);
                });
    }

    @Override
    public void exportInterestTypeToTrello(InterestType interestType) {
        interestType.update(trelloService.saveInterestType(interestType));
        interestTypeRepository.save(interestType);
    }

    @Override
    public void exportInterestTypeToTrello(Long interestTypeId) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType != null)
            exportInterestTypeToTrello(interestType);

    }

    @Override
    public void exportInterestsToTrello() {
        interestRepository.findAll()
                .forEach(e -> {
                    e.update(trelloService.saveInterest(e));
                    interestRepository.save(e);
                });

    }

    @Override
    public void exportInterestToTrello(Interest interest) {
        interest.update(trelloService.saveInterest(interest));
        interestRepository.save(interest);

    }

    @Override
    public void exportInterestToTrello(Long interestId) {
        Interest interest = interestRepository.findOne(interestId);
        if (interest != null)
            exportInterestToTrello(interest);

    }

    @Override
    public void exportTaskTypesToTrello() {
        taskTypeRepository.findAll()
                .forEach(e -> {
                    e.update(trelloService.saveTaskType(e));
                    taskTypeRepository.save(e);
                });

    }

    @Override
    public void exportTaskTypeToTrello(TaskType taskType) {
        taskType.update(trelloService.saveTaskType(taskType));
        taskTypeRepository.save(taskType);

    }

    @Override
    public void exportTaskTypeToTrello(Long taskTypeId) {
        TaskType taskType = taskTypeRepository.findOne(taskTypeId);
        if (taskType != null)
            exportTaskTypeToTrello(taskType);

    }

    @Override
    public void exportTasksToTrello() {
        taskRepository.findAll()
                .forEach(e -> {
                    e.update(trelloService.saveTask(e));
                    taskRepository.save(e);
                });
    }

    @Override
    public void exportTaskToTrello(Task task) {
        task.update(trelloService.saveTask(task));
        taskRepository.save(task);

    }

    @Override
    public void exportTaskToTrello(Long taskId) {
        Task task = taskRepository.findOne(taskId);
        if (task != null)
            exportTaskToTrello(task);
    }


}




