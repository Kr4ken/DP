package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.entity.InterestType;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskType;

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



    // Trello
    void importInterestTypesFromTrello();

    void importInterestTypeFromTrello(InterestType interestType);

    void importInterestTypeFromTrello(String interestTypeTrelloId);

    void importInterestTypeFromTrello(Long interestTypeId);

    void importInterestsFromTrello();

    void importInterestFromTrello(Interest interest);

    void importInterestFromTrello(String interestTrelloId);

    void importInterestFromTrello(Long interestId);

    void importTaskTypesFromTrello();

    void importTaskTypeFromTrello(TaskType taskType);

    void importTaskTypeFromTrello(String taskTypeTrelloId);

    void importTaskTypeFromTrello(Long taskTypeId);

    void importTasksFromTrello();

    void importTaskFromTrello(Task task);

    void importTaskFromTrello(String taskTrelloId);

    void importTaskFromTrello(Long taskId);

    void exportInterestTypesToTrello();

    void exportInterestTypeToTrello(InterestType interestType);

    void exportInterestTypeToTrello(Long interestTypeId);

    void exportInterestsToTrello();

    void exportInterestToTrello(Interest interest);

    void exportInterestToTrello(Long interestId);

    void exportTaskTypesToTrello();

    void exportTaskTypeToTrello(TaskType taskType);

    void exportTaskTypeToTrello(Long taskTypeId);

    void exportTasksToTrello();

    void exportTaskToTrello(Task task);

    void exportTaskToTrello(Long taskId);

    // Хуки

    void scoreTaskFromHabitica(Task task);

    void scoreTaskFromHabitica(String habiticaId);

    void updateFromTrello(String trelloId);
}
