package com.kr4ken.dp.services.impl;

import com.kr4ken.dp.config.DivineConfig;
import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.DivineService;
import com.kr4ken.dp.services.intf.HabiticaService;
import com.kr4ken.dp.services.intf.TrelloService;
import com.kr4ken.dp.utils.TaskUtils;
import com.sun.org.apache.xpath.internal.operations.Div;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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
    private final TaskCheckListRepository taskCheckListRepository;
    private final TaskCheckListItemRepository taskCheckListItemRepository;
    private final TaskSpecialRepository taskSpecialRepository;

    @Autowired
    DivineServiceImplement(
            HabiticaService habiticaService,
            TrelloService trelloService,
            TaskTypeRepository taskTypeRepository,
            TaskRepository taskRepository,
            InterestRepository interestRepository,
            InterestTypeRepository interestTypeRepository,
            TaskCheckListRepository taskCheckListRepository,
            TaskCheckListItemRepository taskCheckListItemRepository,
            TaskSpecialRepository taskSpecialRepository,
            DivineConfig divineConfig

    ) {
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        this.trelloService = trelloService;
        this.habiticaService = habiticaService;
        this.taskCheckListRepository = taskCheckListRepository;
        this.taskCheckListItemRepository = taskCheckListItemRepository;
        this.taskSpecialRepository = taskSpecialRepository;
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
        this.divineConfig = divineConfig;
    }

    // Интересы

    // Перемещает интерес в другую группу в самый верх списка
    private void changeType(Interest interest, InterestType interestType) {
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(interestType);
        interest.setOrd(
                interests.toArray(new Interest[]{})[0].getOrd()/2
        );
        interest.setType(interestType);
    }

    // Ставит в верх списка случайную карточку и типажирует изменения в трелло (Возможно)
    private Interest mixInterest(Long interestTypeId){
        Random random = new Random();
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(interestType);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        int max = interestArray.length - 1, min = 0;
        Integer randomInterest = random.nextInt(max - min + 1) + min;
        changeType(interestArray[randomInterest], interestType);
        // Синхронизация с трелло
        if(divineConfig.getTrelloSync())
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
        InterestType itTarget =newInterestType;
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        changeType(interestArray[0], itTarget);
        // Переносим текущий интерес в список выполнено
        // Синхронизация с трелло
        if(divineConfig.getTrelloSync()) {
            interestRepository.save(trelloService.saveInterest(interestArray[0]));
        }
        else {
            interestRepository.save(interestArray[0]);
        }
        // Возвращаем новый выбранный элемент
        return mixInterest(interestTypeId);
    }


    @Override
    public Interest completeInterests(Long interestTypeId) {
        return moveAndMixInterest(interestTypeId,trelloService.getInterestCompleteType());
    }

    @Override
    public Interest referInterests(Long interestTypeId) {
        return moveAndMixInterest(interestTypeId,trelloService.getInterestReferType());
    }

    @Override
    public Interest dropInterests(Long interestTypeId) {
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        // Переносим текущий интерес в список выполнено
        // Синхронизация с трелло
        if(divineConfig.getTrelloSync()) {
            interestRepository.delete(trelloService.deleteInterest(interestArray[0]));
        }
        else {
            interestRepository.delete(interestArray[0]);
        }
        // Возвращаем новый выбранный элемент
        return mixInterest(interestTypeId);
    }


    // Таски

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
    public void importTaskFromTrello(String trelloId) {
        Task task = new Task();
        task.setTrelloId(trelloId);
        task = trelloService.getTask(task);
        mergeTask(task);
    }

    @Override
    public void importTaskFromTrelloByTrelloId(String trelloId) {
        Optional<Task> task = taskRepository.findByTrelloId(trelloId);
        //Если уже есть то обновляем
        if(task.isPresent())
            importTaskFromTrello(task.get().getId());
        else {
            //Создаем фейковый таск чтобы вытянуть в него все данные
        }



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


    public void exportTaskToHabitica(String trelloId) {
        Optional<Task> task = taskRepository.findByTrelloId(trelloId);
        if(task.isPresent())
            exportTaskToHabitica(task.get());
    }

    public void exportTaskToHabitica(Task task) {
        // Сделаю доп проверку и здесь
        // Пока в хабитику закидываю только из активного списка
        if(trelloService.getActiveList().contains(task.getType())){
           habiticaService.saveTask(task);
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

        importTaskFromTrello(task.getId());


        // Если лист рабочий, то обновляем текущую карточку

        // В конце сохраняем изменения если они были
        habiticaService.saveTask(task);
    }

    @Override
    public void updateFromTrello(String trelloId) {
        importTaskFromTrello(trelloId);
        exportTaskToHabitica(trelloId);
    }

    @Override
    public void exportTaskToHabitica(Long taskId) {

    }

    @Override
    public void importInterestTypesFromTrello() {

    }

    @Override
    public void importInterestTypeFromTrello(InterestType interstType) {

    }

    @Override
    public void importInterestTypeFromTrello(String interestTypeId) {

    }

    @Override
    public void importInterestsFromTrello() {

    }

    @Override
    public void importInterestFromTrello(Interest interest) {

    }

    @Override
    public void importInterestFromTrello(String interestId) {

    }

    @Override
    public void importTaskTypeFromTrello(TaskType taksType) {

    }

    @Override
    public void importTaskTypeFromTrello(String taskTypeId) {

    }

    @Override
    public void importTaskFromTrello(Task task) {

    }

    @Override
    public void exportInterestTypesToTrello() {

    }

    @Override
    public void exportInterestTypeToTrello(InterestType interstType) {

    }

    @Override
    public void exportInterestTypeToTrello(Long interestTypeId) {

    }

    @Override
    public void exportInterestsToTrello() {

    }

    @Override
    public void exportInterestToTrello(Interest interest) {

    }

    @Override
    public void exportInterestToTrello(Long interestId) {

    }

    @Override
    public void exportTaskTypesToTrello() {

    }

    @Override
    public void exportTaskTypeToTrello(TaskType taksType) {

    }

    @Override
    public void exportTaskTypeToTrello(Long taskTypeId) {

    }

    @Override
    public void exportTasksToTrello() {

    }

    @Override
    public void exportTaskToTrello(Task task) {

    }

    @Override
    public void exportTaskToTrello(Long taskId) {

    }
}




