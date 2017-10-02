package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.Task;
import com.kr4ken.dp.models.TaskType;

import java.util.List;

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
    // Удаление
    InterestType deleteInterestType(InterestType intrestType);
    Interest deleteInterest(Interest interest);

    TaskType deleteTaskType(TaskType taskType);

    Interest chooseNewInterest(InterestType interestType);

    void testDeleteAttachment(Interest interest);
}
