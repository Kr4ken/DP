package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.entity.InterestType;
import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskType;

import java.util.List;

/**
 * Сервис который осуществляет синхронизацию внутренних сущностей системы с трелло
 * Не изменяет состояния системы лишь подтягивает и отправляет данные в трелло
 * */

public interface TrelloService {
    // Получение
    List<InterestType> getInterestTypes();
    List<Interest> getInterests();
    Interest getInterest(Interest interest);
    InterestType getInterestType(InterestType interestType);

    List<TaskType> getTaskTypes();
    List<Task> getTasks();
    TaskType getTaskType(TaskType taskType);
    Task getTask(Task taks);

    // Сохранение
    InterestType saveInterestType(InterestType intrestType);
    Interest saveInterest(Interest intrest);

    TaskType saveTaskType(TaskType taskType);
    Task saveTask(Task task);
    // Удаление
    InterestType deleteInterestType(InterestType intrestType);
    Interest deleteInterest(Interest interest);

    TaskType deleteTaskType(TaskType taskType);
    Task deleteTask(Task task);

    Interest chooseNewInterest(InterestType interestType);

    void testDeleteAttachment(Interest interest);
}
