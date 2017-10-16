package com.kr4ken.dp.services.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.config.HabiticaConfig;
import com.kr4ken.dp.config.TrelloConfig;
import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.HabiticaService;
import com.kr4ken.dp.services.intf.TrelloService;
import com.kr4ken.habitica.Habitica;
import com.kr4ken.habitica.impl.HabiticaImpl;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableIndexAnyGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HabiticaServiceImplement implements HabiticaService {
    private final HabiticaConfig habiticaConfig;
    private final Habitica habiticaApi;
    private final TaskTypeRepository taskTypeRepository;

    @Autowired
    HabiticaServiceImplement(
            HabiticaConfig habiticaConfig,
            TaskTypeRepository taskTypeRepository
    ) {
        this.habiticaConfig = habiticaConfig;
        this.taskTypeRepository = taskTypeRepository;
        // Подгрузка конфигурации
        habiticaApi = new HabiticaImpl(habiticaConfig.getApiUser(),habiticaConfig.getApiKey());
    }
    @Override
    public List<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for(com.kr4ken.habitica.domain.Task task:habiticaApi.getUserTasks()){
            result.add(new Task( task.getAlias(),
                    task.getText(),
                    task.getNotes(),
                    null,
                    false,
                    false,
                    null,
                    taskTypeRepository.findOne(1L),
                    null,
                    null,
                    TaskAttribute.valueOf(task.getAttribute().replace(task.getAttribute().charAt(0),Character.toUpperCase(task.getAttribute().charAt(0)))),
                    1.
            ));
        }
        return result;
    }
}

