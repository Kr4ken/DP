package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.TaskNotFoundException;
import com.kr4ken.dp.exceptions.TaskTypeNotFoundException;
import com.kr4ken.dp.models.*;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskRestController {

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final TrelloService trelloService;

    private final TaskCheckListRepository taskCheckListRepository;
    private final TaskCheckListItemRepository taskCheckListItemRepository;
    private final TaskSpecialRepository taskSpecialRepository;

    @Autowired
    public TaskRestController(TaskTypeRepository taskTypeRepository, TaskRepository taskRepository, TrelloService trelloService, TaskCheckListRepository taskCheckListRepository, TaskCheckListItemRepository taskCheckListItemRepository, TaskSpecialRepository taskSpecialRepository) {
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        this.trelloService = trelloService;
        this.taskCheckListRepository = taskCheckListRepository;
        this.taskCheckListItemRepository = taskCheckListItemRepository;
        this.taskSpecialRepository = taskSpecialRepository;
    }

    private void trelloSync(Task task) {
        taskRepository.save(trelloService.saveTask(task));
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Task> readTasks() {
        return taskRepository.findAll();
    }

    private void mergeChecklistItems(List<TaskCheckListItem> taskCheckListItems) {
        for (TaskCheckListItem taskCheckListItem : taskCheckListItems) {
            Optional<TaskCheckListItem> current = taskCheckListItemRepository.findByTrelloId(taskCheckListItem.getTrelloId());
            if (current.isPresent()) {
                current.get().copy(current.get());
                taskCheckListItemRepository.save(current.get());
            } else {
                taskCheckListItemRepository.save(taskCheckListItem);
            }
        }
    }


    private void mergeChecklists(List<TaskCheckList> taskCheckLists) {
        for (TaskCheckList taskCheckList : taskCheckLists) {
            Optional<TaskCheckList> current = taskCheckListRepository.findByTrelloId(taskCheckList.getTrelloId());
            if (current.isPresent()) {
                if (taskCheckList.getChecklistItems() != null) {
                    mergeChecklistItems(current.get().getChecklistItems());
                }
                current.get().copy(taskCheckList);
                taskCheckListRepository.save(current.get());
            } else {
                if (taskCheckList.getChecklistItems() != null) {
                    mergeChecklistItems(taskCheckList.getChecklistItems());
                }
                taskCheckListRepository.save(taskCheckList);
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Task input, @RequestParam(required = false) Optional<Boolean> trello) {
        // Сперва добавляем новую особенность которая соответсвует новой задаче
        if(input.getSpecial() != null) input.setSpecial(taskSpecialRepository.save(input.getSpecial()));
        if(input.getChecklists() != null) mergeChecklists(input.getChecklists());
        Task result = taskRepository.save(new Task(input));
        if (trello.isPresent() && trello.get()) {
            trelloSync(result);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{taskId}")
    ResponseEntity<?> update(@PathVariable Long taskId, @RequestBody Task input, @RequestParam(required = false) Optional<Boolean> trello) {
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            return new ResponseEntity(new TaskNotFoundException(taskId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        task.copy(input);

        taskRepository.save(task);
        if (trello.isPresent() && trello.get()) {
            trelloSync(task);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{taskId}")
    ResponseEntity<?> delete(@PathVariable Long taskId, @RequestParam(required = false) Optional<Boolean> trello) {

        Task task =taskRepository.findOne(taskId);
        if (task == null) {
            return new ResponseEntity(new TaskNotFoundException(taskId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        if (trello.isPresent() && trello.get()) {
            trelloService.deleteTask(task);
        }
        taskRepository.delete(task);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{taskId}")
    public Task readInterest(@PathVariable Long taskId) {
        return this.taskRepository.findOne(taskId);
    }
}

