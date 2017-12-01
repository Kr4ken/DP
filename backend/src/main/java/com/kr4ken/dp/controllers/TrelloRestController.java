package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.services.intf.DivineService;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Контроллер отвечающий за взаимодействие с трелло
 */
@RestController
@RequestMapping("/trello")
public class TrelloRestController {

    private final TrelloService trelloService;

    private final DivineService divineService;

    @Autowired
    TrelloRestController(TrelloService trelloService,
                         DivineService divineService
    ) {
        this.divineService = divineService;
        this.trelloService = trelloService;
    }

    // Запросы

    @RequestMapping(method = RequestMethod.GET, value = "/tasks")
    Collection<Task> getTasks() {
        return trelloService.getTasks();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/interests")
    Collection<Interest> getInterests() {
        return trelloService.getInterests();
    }


    //Импорт

    @RequestMapping(method = RequestMethod.POST, value = "/import")
    ResponseEntity<?> trelloImport() {
        trelloImportInterestTypes();
        trelloImportInterests();
        trelloImportTaskTypes();
        trelloImportTasks();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Интересы
    @RequestMapping(method = RequestMethod.POST, value = "/import/interestTypes")
    ResponseEntity<?> trelloImportInterestTypes() {
        divineService.importInterestTypesFromTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interestTypes/{interestTypeId}")
    ResponseEntity<?> trelloImportInterestType(@PathVariable Long interestTypeId) {
        divineService.importInterestTypeFromTrello(interestTypeId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interests")
    ResponseEntity<?> trelloImportInterests() {
        divineService.importInterestsFromTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/interests/{interestId}")
    ResponseEntity<?> trelloImportInterest(@PathVariable Long interestId) {
        divineService.importInterestFromTrello(interestId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Таски
    @RequestMapping(method = RequestMethod.POST, value = "/import/taskTypes")
    ResponseEntity<?> trelloImportTaskTypes() {
        divineService.importTaskTypesFromTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/taskTypes/{taskTypeId}")
    ResponseEntity<?> trelloImportTaskType(@PathVariable Long taskTypeId) {
        divineService.importTaskTypeFromTrello(taskTypeId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/tasks")
    ResponseEntity<?> trelloImportTasks() {
        divineService.importTasksFromTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import/task/{taskId}")
    ResponseEntity<?> trelloImportTask(@PathVariable Long taskId) {
        divineService.importTaskFromTrello(taskId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }


    // Экспорт

    @RequestMapping(method = RequestMethod.POST, value = "/export")
    ResponseEntity<?> trelloExport() {
        trelloExportInterestTypes();
        trelloExportInterests();
        trelloExportTaskTypes();
        trelloExportTasks();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interestTypes")
    ResponseEntity<?> trelloExportInterestTypes() {
        divineService.exportInterestTypesToTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interestTypes/{interestTypeId}")
    ResponseEntity<?> trelloExportInterestType(@PathVariable Long interestTypeId) {
        divineService.exportInterestTypeToTrello(interestTypeId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interests")
    ResponseEntity<?> trelloExportInterests() {
        divineService.exportInterestsToTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/interests/{interestId}")
    ResponseEntity<?> trelloExportInterest(@PathVariable Long interestId) {
        divineService.exportInterestToTrello(interestId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Таски
    @RequestMapping(method = RequestMethod.POST, value = "/export/taskTypes")
    ResponseEntity<?> trelloExportTaskTypes() {
        divineService.exportTaskTypesToTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/taskTypes/{taskTypeId}")
    ResponseEntity<?> trelloExportTaskType(@PathVariable Long taskTypeId) {
        divineService.exportTaskTypeToTrello(taskTypeId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/tasks")
    ResponseEntity<?> trelloExportTasks() {
        divineService.exportTasksToTrello();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export/task/{taskId}")
    ResponseEntity<?> trelloExportTask(@PathVariable Long taskId) {
        divineService.exportTaskToTrello(taskId);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }


}
