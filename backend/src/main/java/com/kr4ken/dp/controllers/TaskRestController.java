package com.kr4ken.dp.controllers;

import com.kr4ken.dp.config.DivineConfig;
import com.kr4ken.dp.exceptions.TaskNotFoundException;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.repository.TaskRepository;
import com.kr4ken.dp.models.repository.TaskTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

/**
 * Контроллер предоставляющий рестфул интерфейс
 * Для объектов Task - Задача
 */
@RestController
@RequestMapping("/tasks")
public class TaskRestController {

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final TrelloService trelloService;

    private final DivineConfig divineConfig;

    @Autowired
    public TaskRestController(TaskTypeRepository taskTypeRepository,
                              TaskRepository taskRepository,
                              TrelloService trelloService,
                              DivineConfig divineConfig) {
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        this.trelloService = trelloService;
        this.divineConfig = divineConfig;
    }

    private void trelloSync(Task task, Optional<Boolean> trello) {
        Boolean sync = trello.isPresent() ? trello.get() : divineConfig.getTrelloSync();
        if (sync)
            taskRepository.save(trelloService.saveTask(task));
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Task> readTasks(@RequestParam(required = false) Long type) {
        if (type != null)
            return taskRepository.findByType(taskTypeRepository.findOne(type));
        else
            return taskRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Task input, @RequestParam(required = false) Optional<Boolean> trello) {
        Task result = taskRepository.save(new Task(input));
        trelloSync(result, trello);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(input.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{taskId}")
    ResponseEntity<?> update(@PathVariable Long taskId, @RequestBody Task newTask, @RequestParam(required = false) Optional<Boolean> trello) {
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            return new ResponseEntity(new TaskNotFoundException(taskId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        task.update(newTask);
        taskRepository.save(task);
        trelloSync(task, trello);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{taskId}")
    ResponseEntity<?> delete(@PathVariable Long taskId, @RequestParam(required = false) Optional<Boolean> trello) {

        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            return new ResponseEntity(new TaskNotFoundException(taskId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        Boolean sync = trello.isPresent() ? trello.get() : divineConfig.getTrelloSync();
        if (sync) {
            trelloService.delete(task);
        }
        taskRepository.delete(task);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{taskId}")
    public Task readInterest(@PathVariable Long taskId) {
        return this.taskRepository.findOne(taskId);
    }
}

