package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.entity.Task;

import java.util.List;

/**
 * Сервис который осуществляет синхронизацию внутренних сущностей системы с Habitica
 * Не изменяет состояния системы лишь подтягивает и отправляет данные в Habitica
 * */

public interface HabiticaService {
    // Получение
    List<Task> getTasks();

    List<Task> getTrelloTasks();
}
