package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.TaskRepository;
import com.kr4ken.dp.services.intf.HabiticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/habitica")
public class HabiticaRestController {

    private final HabiticaService habiticaService;
    private final TaskRepository taskRepository;


    @Autowired
    HabiticaRestController(HabiticaService habiticaService,
                           TaskRepository taskRepository
    ) {
        this.habiticaService = habiticaService;
        this.taskRepository = taskRepository;
    }

    @RequestMapping(value = "/tasks",method = RequestMethod.GET)
    Collection<Task> readInterests() {
        return habiticaService.getTasks();
    }

    @RequestMapping(value = "/trello/tasks",method = RequestMethod.GET)
    Collection<Task> readTrelloInterests() {
        return habiticaService.getTrelloTasks();
    }


    @RequestMapping(value = "/tasks/{taskId}",method = RequestMethod.POST)
    Task readTrelloInterests(@PathVariable Long taskId) {
        return habiticaService.saveTask(taskRepository.findOne(taskId));
    }
}
