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

    List<TaskType> getTaskTypes();
    List<Task> getTasks();

    // Обновление
    Interest getInterest(Interest interest);
    InterestType getInterestType(InterestType interestType);

    TaskType getTaskType(TaskType taskType);
    Task getTask(Task taks);

    // Сохранение
    // Возвращает объекты с изменениями которые произошли при сохранении
    // Меняется URL картинки на внутренний трелло, и цепдяется ТреллоИд
    InterestType saveInterestType(InterestType intrestType);
    Interest saveInterest(Interest intrest);

    TaskType saveTaskType(TaskType taskType);
    Task saveTask(Task task);

    // Удаление
    InterestType deleteInterestType(InterestType intrestType);
    Interest deleteInterest(Interest interest);

    TaskType deleteTaskType(TaskType taskType);
    Task deleteTask(Task task);

    // Вспомогательное
    //Получить список типов задач, которые сейчас активны
    List<TaskType> getActiveList();

}
