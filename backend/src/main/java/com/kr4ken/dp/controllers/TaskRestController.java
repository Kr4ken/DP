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
                current.get().copy(taskCheckListItem);
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

    private void mergeTask(Task task) {
        Optional<Task> current = taskRepository.findByTrelloId(task.getTrelloId());
        // Проверяем есть ли уже текущий элемент в моей базе
        // Если есть то просто обновляем текущее значение
        if (current.isPresent()) {
            current.get().copy(current.get());
            if (task.getSpecial() != null) {
                current.get().getSpecial().copy(task.getSpecial());
                taskSpecialRepository.save(current.get().getSpecial());
            }
            if (task.getChecklists() != null) {
                mergeChecklists(current.get().getChecklists());
            }
            taskRepository.save(current.get());
        }
        // Если нет то просто сохраняем
        else {
            if (task.getSpecial() != null) {
                taskSpecialRepository.save(task.getSpecial());
            }
            if (task.getChecklists() != null) {
                mergeChecklists(task.getChecklists());
            }
            taskRepository.save(task);
        }

    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Task input, @RequestParam(required = false) Optional<Boolean> trello) {
        // Сперва добавляем новую особенность которая соответсвует новой задаче
        mergeTask(input);
        Task result = input;
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
        mergeTask(task);
//        taskRepository.save(task);
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

        Task temp = task;
        taskRepository.delete(task);
        for(TaskCheckList checklist:temp.getChecklists()){
           taskCheckListRepository.delete(checklist);
           for(TaskCheckListItem item:checklist.getChecklistItems())
               taskCheckListItemRepository.delete(item);
        }
        taskSpecialRepository.delete(temp.getSpecial());

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{taskId}")
    public Task readInterest(@PathVariable Long taskId) {
        return this.taskRepository.findOne(taskId);
    }
}

