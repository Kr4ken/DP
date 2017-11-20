package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.entity.InterestType;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskType;
import com.kr4ken.dp.models.entity.*;

import java.util.List;

/**
 * Сервис обеспечивающий всю логику при работе приложения
 */
public interface DivineService {
    // Интересы

    Interest mixInterests(Long interestTypeId);
    Interest completeInterests(Long interestTypeId);
    Interest referInterests(Long interestTypeId);
    Interest dropInterests(Long interestTypeId);

    // Хабитика

    void exportTasksToHabitica();
    void exportTaskToHabitica(Task task);
    void exportTaskToHabitica(Long taskId);

    void scoreTaskFromHabitica(Task task);
    void scoreTaskFromHabitica(String habiticaId);


    // Trello
    void importInterestTypesFromTrello();
    void importInterestTypeFromTrello(InterestType interstType);
    void importInterestTypeFromTrello(String interestTypeId);
    void importInterestsFromTrello();
    void importInterestFromTrello(Interest interest);
    void importInterestFromTrello(String interestId);
    void importTaskTypesFromTrello();
    void importTaskTypeFromTrello(TaskType  taksType);
    void importTaskTypeFromTrello(String taskTypeId);
    void importTasksFromTrello();
    void importTaskFromTrello(Task task);
    void importTaskFromTrello(String taskId);

    void exportInterestTypesToTrello();
    void exportInterestTypeToTrello(InterestType interstType);
    void exportInterestTypeToTrello(Long interestTypeId);
    void exportInterestsToTrello();
    void exportInterestToTrello(Interest interest);
    void exportInterestToTrello(Long interestId);
    void exportTaskTypesToTrello();
    void exportTaskTypeToTrello(TaskType  taksType);
    void exportTaskTypeToTrello(Long taskTypeId);
    void exportTasksToTrello();
    void exportTaskToTrello(Task task);
    void exportTaskToTrello(Long taskId);

    // TODO: Удалить все что ниже
    // Импорт
    // Таски
//    void importTasksFromTrello();

    void importTaskFromTrello(Long id);

//    void importTaskFromTrello(String trelloId);

    void importTaskFromTrelloByTrelloId(String trelloId);

//    void importTaskTypesFromTrello();

    void importTaskTypeFromTrello(Long id);

    // Habitica


    void TasksTrelloToHabitica();


    void updateFromTrello(Task task);

    void updateFromTrello(String trelloId);
}
