package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.entity.InterestType;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskType;

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

    // Trello

    // Импорт
    // Таски
    void importTasksFromTrello();

    void importTaskFromTrello(Long id);

    void importTaskFromTrello(String trelloId);

    void importTaskFromTrelloByTrelloId(String trelloId);

    void importTaskTypesFromTrello();

    void importTaskTypeFromTrello(Long id);

    // Habitica

    void exportTasksToHabitica();

    void TasksTrelloToHabitica();

    void scoreTaskFromHabitica(Task task);

    void scoreTaskFromHabitica(String habiticaId);


    void updateFromTrello(Task task);

    void updateFromTrello(String trelloId);
}
