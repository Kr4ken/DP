package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.TaskTypeNotFoundException;
import com.kr4ken.dp.models.entity.TaskType;
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
 * Для объектов TaskType - Тип задачи
 */
@RestController
@RequestMapping("/taskTypes")
public class TaskTypeRestController {

    private final TaskTypeRepository taskTypeRepository;
    private final TrelloService trelloService;

    @Autowired
    TaskTypeRestController(TaskTypeRepository taskTypeRepository,
                           TrelloService trelloService) {
        this.taskTypeRepository = taskTypeRepository;
        this.trelloService = trelloService;
    }

    private void trelloSync(TaskType taskType) {
        taskTypeRepository.save(trelloService.saveTaskType(taskType));
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<TaskType> readTaskTypes() {
        return taskTypeRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody TaskType input, @RequestParam(required = false) Optional<Boolean> trello) {
        TaskType result = taskTypeRepository.save(new TaskType(input));
        if (trello.isPresent() && trello.get()) {
            trelloSync(result);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{taskTypeId}")
    ResponseEntity<?> update(@PathVariable Long taskTypeId, @RequestBody TaskType input, @RequestParam(required = false) Optional<Boolean> trello) {
        TaskType taskType = taskTypeRepository.findOne(taskTypeId);
        if (taskType == null) {
            return new ResponseEntity(new TaskTypeNotFoundException(taskTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        taskType.update(input);

        taskTypeRepository.save(taskType);
        if (trello.isPresent() && trello.get()) {
            trelloSync(taskType);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(taskType.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{taskTypeid}")
    ResponseEntity<?> delete(@PathVariable Long taskTypeId, @RequestParam(required = false) Optional<Boolean> trello) {

        TaskType taskType = taskTypeRepository.findOne(taskTypeId);
        if (taskType == null) {
            return new ResponseEntity(new TaskTypeNotFoundException(taskTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        if (trello.isPresent() && trello.get()) {
            trelloService.deleteTaskType(taskType);
        }
        taskTypeRepository.delete(taskTypeId);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{taskTypeId}")
    public TaskType readInterestType(@PathVariable Long taskType) {
        return this.taskTypeRepository.findOne(taskType);
    }
}

