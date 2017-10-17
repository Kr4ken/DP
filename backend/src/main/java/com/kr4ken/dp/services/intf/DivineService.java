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
    void importTasksFromTrello();

    void importTaskFromTrello(Long id);

    void importTaskTypesFromTrello();

    void importTaskTypeFromTrello(Long id);

    void TasksTrelloToHabitica();

}
