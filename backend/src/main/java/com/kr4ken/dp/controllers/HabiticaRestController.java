package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.TaskRepository;
import com.kr4ken.dp.services.intf.DivineService;
import com.kr4ken.dp.services.intf.HabiticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/habitica")
public class HabiticaRestController {

    private final HabiticaService habiticaService;
    private final DivineService divineService;
    private final TaskRepository taskRepository;


    @Autowired
    HabiticaRestController(HabiticaService habiticaService,
                           TaskRepository taskRepository,
                           DivineService divineService
    ) {
        this.habiticaService = habiticaService;
        this.taskRepository = taskRepository;
        this.divineService = divineService;
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

    // Export

    @RequestMapping(value = "/export/tasks",method = RequestMethod.POST)
    ResponseEntity exportTasks() {
        divineService.exportTasksToHabitica();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }


}
