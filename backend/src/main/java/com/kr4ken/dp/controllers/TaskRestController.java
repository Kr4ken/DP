package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.TaskNotFoundException;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.entity.TaskCheckList;
import com.kr4ken.dp.models.entity.TaskCheckListItem;
import com.kr4ken.dp.models.repository.*;
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
import java.util.OptionalLong;

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

    // TODO: допилить
    private void trelloSync(Task task) {
        taskRepository.save(trelloService.saveTask(task));
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Task> readTasks(@RequestParam(required = false) Long type) {
        if (type != null)
            return taskRepository.findByType(taskTypeRepository.findOne(type));
        else
            return taskRepository.findAll();
    }

//    private void mergeChecklistItems(List<TaskCheckListItem> taskCheckListItems) {
//        for (TaskCheckListItem taskCheckListItem : taskCheckListItems) {
//            Optional<TaskCheckListItem> current = taskCheckListItemRepository.findByTrelloId(taskCheckListItem.getTrelloId());
//            if (current.isPresent()) {
//                current.get().copy(taskCheckListItem);
//                taskCheckListItemRepository.save(current.get());
//            } else {
//                taskCheckListItemRepository.save(taskCheckListItem);
//            }
//        }
//    }
//
//    private void mergeChecklists(List<TaskCheckList> taskCheckLists) {
//        for (TaskCheckList taskCheckList : taskCheckLists) {
//            Optional<TaskCheckList> current = taskCheckListRepository.findByTrelloId(taskCheckList.getTrelloId());
//            if (current.isPresent()) {
//                if (taskCheckList.getChecklistItems() != null) {
//                    mergeChecklistItems(current.get().getChecklistItems());
//                }
//                current.get().copy(taskCheckList);
//                taskCheckListRepository.save(current.get());
//            } else {
//                if (taskCheckList.getChecklistItems() != null) {
//                    mergeChecklistItems(taskCheckList.getChecklistItems());
//                }
//                taskCheckListRepository.save(taskCheckList);
//            }
//        }
//    }
//
//    private void mergeTask(Task task) {
//        Optional<Task> current = taskRepository.findByTrelloId(task.getTrelloId());
//        // Проверяем есть ли уже текущий элемент в моей базе
//        // Если есть то просто обновляем текущее значение
//        if (current.isPresent()) {
//            current.get().copy(current.get());
//            if (task.getSpecial() != null) {
//                current.get().getSpecial().copy(task.getSpecial());
//                taskSpecialRepository.save(current.get().getSpecial());
//            }
//            if (task.getChecklists() != null) {
//                mergeChecklists(current.get().getChecklists());
//            }
//            taskRepository.save(current.get());
//        }
//        // Если нет то просто сохраняем
//        else {
//            if (task.getSpecial() != null) {
//                taskSpecialRepository.save(task.getSpecial());
//            }
//            if (task.getChecklists() != null) {
//                mergeChecklists(task.getChecklists());
//            }
//            taskRepository.save(task);
//        }
//
//    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Task input, @RequestParam(required = false) Object  trello) {
        // Добавляем новую особенность которая соответсвует новой задаче
        if (input.getSpecial() != null) {
            taskSpecialRepository.save(input.getSpecial());
        }
        // Добавляем чеклисты
        if (input.getChecklists() != null) {
            for (TaskCheckList taskCheckList : input.getChecklists()) {
                if (taskCheckList.getChecklistItems() != null) {
                    taskCheckListItemRepository.save(taskCheckList.getChecklistItems());
                }
                taskCheckListRepository.save(taskCheckList);
            }
        }

        taskRepository.save(input);
        if (trello != null) {
            trelloSync(input);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(input.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{taskId}")
    ResponseEntity<?> update(@PathVariable Long taskId, @RequestBody Task newTask, @RequestParam(required = false) Optional<Boolean> trello) {
        Task oldTask = taskRepository.findOne(taskId);
        if (oldTask == null) {
            return new ResponseEntity(new TaskNotFoundException(taskId.toString()),
                    HttpStatus.NOT_FOUND);
        }
//        Удаляем старые чеклисты если есть новые
        if (newTask.getChecklists() != null) {
            oldTask.getChecklists().clear();
            for (TaskCheckList taskCheckList : oldTask.getChecklists()) {
                taskCheckListRepository.delete(taskCheckList);
//                if(taskCheckList.getChecklistItems()!= null) {
//                    for (TaskCheckListItem taskCheckListItem : taskCheckList.getChecklistItems()){
//                        taskCheckListItemRepository.delete(taskCheckListItem);
//                    }
//                }
            }
            // А потом добавляем новые
            for (TaskCheckList taskCheckList : newTask.getChecklists()) {
                if (taskCheckList.getChecklistItems() != null) {
                    for (TaskCheckListItem taskCheckListItem : taskCheckList.getChecklistItems()) {
                        taskCheckListItemRepository.save(taskCheckListItem);
                    }
                }
                taskCheckListRepository.save(taskCheckList);
            }
        }

        if (newTask.getSpecial() != null) {
            taskSpecialRepository.delete(newTask.getSpecial());
            taskSpecialRepository.save(newTask.getSpecial());
        }
        // Обновление
        oldTask.copy(newTask);
//        mergeTask(task);
        taskRepository.save(oldTask);
        if (trello.isPresent() && trello.get()) {
            trelloSync(oldTask);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(oldTask.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{taskId}")
    ResponseEntity<?> delete(@PathVariable Long taskId, @RequestParam(required = false) Optional<Boolean> trello) {

        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            return new ResponseEntity(new TaskNotFoundException(taskId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        task.getChecklists().clear();

        if (trello.isPresent() && trello.get()) {
            trelloService.deleteTask(task);
        }

        Task temp = task;
        taskRepository.delete(task);
        for (TaskCheckList checklist : temp.getChecklists()) {
            taskCheckListRepository.delete(checklist);
            for (TaskCheckListItem item : checklist.getChecklistItems())
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

