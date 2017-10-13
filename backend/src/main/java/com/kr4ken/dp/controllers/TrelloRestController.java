package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trello")
public class TrelloRestController {

    private final InterestTypeRepository interestTypeRepository;
    private final InterestRepository interestRepository;
    private final TrelloService trelloService;

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final TaskCheckListRepository taskCheckListRepository;
    private final TaskCheckListItemRepository taskCheckListItemRepository;
    private final TaskSpecialRepository taskSpecialRepository;

    @Autowired
    TrelloRestController(InterestTypeRepository interestTypeRepository,
                         InterestRepository interestRepository,
                         TrelloService trelloService,
                         TaskTypeRepository taskTypeRepository,
                         TaskCheckListRepository taskCheckListRepository,
                         TaskCheckListItemRepository taskCheckListItemRepository,
                         TaskSpecialRepository taskSpecialRepository,
                         TaskRepository taskRepository
    ) {
        this.interestTypeRepository = interestTypeRepository;
        this.interestRepository = interestRepository;
        this.trelloService = trelloService;
        this.taskTypeRepository = taskTypeRepository;
        this.taskRepository = taskRepository;
        this.taskCheckListItemRepository = taskCheckListItemRepository;
        this.taskCheckListRepository = taskCheckListRepository;
        this.taskSpecialRepository = taskSpecialRepository;
    }

    //Импорт

    @RequestMapping(method = RequestMethod.POST, value = "/import")
    ResponseEntity<?> trelloImport() {
        trelloImportInterestTypes();
        trelloImportInterests();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interestTypes")
    ResponseEntity<?> trelloImportInterestTypes() {
        trelloService.getInterestTypes()
                .forEach(e -> {
                    Optional<InterestType> current = interestTypeRepository.findByTrelloId(e.getTrelloId());
                    if (current.isPresent()) {
                        current.get().copy(e);
                        interestTypeRepository.save(current.get());
                    } else {
                        interestTypeRepository.save(e);
                    }
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interestTypes/{interestTypeId}")
    ResponseEntity<?> trelloImportInterestType(@PathVariable Long interestTypeId) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        interestType.copy(trelloService.getInterestType(interestType));
        interestTypeRepository.save(interestType);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interests")
    ResponseEntity<?> trelloImportInterests() {
        trelloService.getInterests()
                .forEach(e -> {
                    Optional<Interest> current = interestRepository.findByTrelloId(e.getTrelloId());
                    if (current.isPresent()) {
                        current.get().copy(e);
                        interestRepository.save(current.get());
                    } else {
                        interestRepository.save(e);
                    }
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interests/{interestId}")
    ResponseEntity<?> trelloImportInterest(@PathVariable Long interestId) {
        Interest interest = interestRepository.findOne(interestId);
        interest.copy(trelloService.getInterest(interest));
        interestRepository.save(interest);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Таски
    @RequestMapping(method = RequestMethod.POST, value = "/import/taskTypes")
    ResponseEntity<?> trelloImportTaskTypes() {
        trelloService.getTaskTypes()
                .forEach(e -> {
                    Optional<TaskType> current = taskTypeRepository.findByTrelloId(e.getTrelloId());
                    if (current.isPresent()) {
                        current.get().copy(e);
                        taskTypeRepository.save(current.get());
                    } else {
                        taskTypeRepository.save(e);
                    }
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/taskTypes/{taskTypeId}")
    ResponseEntity<?> trelloImportTaskType(@PathVariable Long taskTypeId) {
        TaskType taskType = taskTypeRepository.findOne(taskTypeId);
        taskType.copy(trelloService.getTaskType(taskType));
        taskTypeRepository.save(taskType);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
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
                current.get().copy(taskCheckList);
                taskCheckListRepository.save(current.get());
                if (taskCheckList.getChecklistItems() != null) {
                    mergeChecklistItems(current.get().getChecklistItems());
                }
            } else {
                taskCheckListRepository.save(taskCheckList);
                if (taskCheckList.getChecklistItems() != null) {
                    mergeChecklistItems(taskCheckList.getChecklistItems());
                }
            }
        }
    }

    private void mergeTask(Task task) {
        Optional<Task> current = taskRepository.findByTrelloId(task.getTrelloId());
        // Проверяем есть ли уже текущий элемент в моей базе
        // Если есть то просто обновляем текущее значение
        if (current.isPresent()) {
            current.get().copy(task);
            taskRepository.save(current.get());
            if (task.getSpecial() != null) {
                current.get().getSpecial().copy(task.getSpecial());
                taskSpecialRepository.save(current.get().getSpecial());
            }
            if (task.getChecklists() != null) {
                mergeChecklists(current.get().getChecklists());
            }
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

    @RequestMapping(method = RequestMethod.POST, value = "/import/tasks")
    ResponseEntity<?> trelloImportTasks() {
        trelloService.getTasks()
                .forEach(this::mergeTask);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/task/{taskId}")
    ResponseEntity<?> trelloImportTask(@PathVariable Long taskId) {
        Task task = taskRepository.findOne(taskId);
        mergeTask(task);
//        task.copy(trelloService.getTask(task));
//        taskRepository.save(task);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }


    // Экспорт

    @RequestMapping(method = RequestMethod.POST, value = "/export")
    ResponseEntity<?> trelloExport() {
        trelloExportInterestTypes();
        trelloExportInterests();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interestTypes")
    ResponseEntity<?> trelloExportInterestTypes() {
        interestTypeRepository.findAll()
                .stream()
                .forEach(e -> {
                    e.copy(trelloService.saveInterestType(e));
                    interestTypeRepository.save(e);
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interestTypes/{interestTypeId}")
    ResponseEntity<?> trelloExportInterestType(@PathVariable Long interestTypeId) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        interestType.copy(trelloService.saveInterestType(interestType));
        interestTypeRepository.save(interestType);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interests")
    ResponseEntity<?> trelloExportInterests() {
        interestRepository.findAll()
                .stream()
                .forEach(e -> {
                    e.copy(trelloService.saveInterest(e));
                    interestRepository.save(e);
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/export/{interestId}")
    ResponseEntity<?> trelloExportInterest(@PathVariable Long interestId) {
        Interest interest = interestRepository.findOne(interestId);
        interest.copy(trelloService.saveInterest(interest));
        interestRepository.save(interest);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
