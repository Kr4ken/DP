package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.*;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/trello")
public class TrelloRestController {

    private final InterestTypeRepository interestTypeRepository;
    private final InterestRepository interestRepository;
    private final TrelloService trelloService;

    private final TaskTypeRepository taskTypeRepository;

    @Autowired
    TrelloRestController(InterestTypeRepository interestTypeRepository,
                         InterestRepository interestRepository,
                         TrelloService trelloService,
                         TaskTypeRepository taskTypeRepository) {
        this.interestTypeRepository = interestTypeRepository;
        this.interestRepository = interestRepository;
        this.trelloService = trelloService;
        this.taskTypeRepository = taskTypeRepository;
    }

    //Импорт

    @RequestMapping(method = RequestMethod.POST, value ="/import" )
    ResponseEntity<?> trelloImport(){
        trelloImportInterestTypes();
        trelloImportInterests();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/import/interestTypes" )
    ResponseEntity<?> trelloImportInterestTypes(){
        trelloService.getInterestTypes()
                .forEach(e -> {
                    Optional<InterestType> current = interestTypeRepository.findByTrelloId(e.getTrelloId());
                    if(current.isPresent()) {
                        current.get().copy(e);
                        interestTypeRepository.save(current.get());
                    }
                    else{
                        interestTypeRepository.save(e);
                    }
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/import/interestTypes/{interestTypeId}" )
    ResponseEntity<?> trelloImportInterestType(@PathVariable Long interestTypeId){
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        interestType.copy(trelloService.getInterestType(interestType));
        interestTypeRepository.save(interestType);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/import/interests" )
    ResponseEntity<?> trelloImportInterests(){
        trelloService.getInterests()
                .forEach(e -> {
                    Optional<Interest> current = interestRepository.findByTrelloId(e.getTrelloId());
                    if(current.isPresent()) {
                        current.get().copy(e);
                        interestRepository.save(current.get());
                    }
                    else{
                        interestRepository.save(e);
                    }
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/import/interests/{interestId}" )
    ResponseEntity<?> trelloImportInterest(@PathVariable Long interestId){
        Interest interest = interestRepository.findOne(interestId);
        interest.copy(trelloService.getInterest(interest));
        interestRepository.save(interest);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

        // Таски
    @RequestMapping(method = RequestMethod.POST, value ="/import/taskTypes" )
    ResponseEntity<?> trelloImportTaskTypes(){
        trelloService.getTaskTypes()
                .forEach(e -> {
                    Optional<TaskType> current = taskTypeRepository.findByTrelloId(e.getTrelloId());
                    if(current.isPresent()) {
                        current.get().copy(e);
                        taskTypeRepository.save(current.get());
                    }
                    else{
                        taskTypeRepository.save(e);
                    }
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/import/taskTypes/{taskTypeId}" )
    ResponseEntity<?> trelloImportTaskType(@PathVariable Long taskTypeId){
        TaskType taskType = taskTypeRepository.findOne(taskTypeId);
        taskType.copy(trelloService.getTaskType(taskType));
        taskTypeRepository.save(taskType);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }



    // Экспорт

    @RequestMapping(method = RequestMethod.POST, value ="/export" )
    ResponseEntity<?> trelloExport(){
        trelloExportInterestTypes();
        trelloExportInterests();
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/export/interestTypes" )
    ResponseEntity<?> trelloExportInterestTypes(){
        interestTypeRepository.findAll()
                .stream()
                .forEach( e ->{
                    e.copy(trelloService.saveInterestType(e));
                    interestTypeRepository.save(e);
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/export/interestTypes/{interestTypeId}" )
    ResponseEntity<?> trelloExportInterestType(@PathVariable Long interestTypeId){
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        interestType.copy(trelloService.saveInterestType(interestType));
        interestTypeRepository.save(interestType);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/export/interests" )
    ResponseEntity<?> trelloExportInterests(){
        interestRepository.findAll()
                .stream()
                .forEach( e ->{
                    e.copy(trelloService.saveInterest(e));
                    interestRepository.save(e);
                });
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST, value ="/import/export/{interestId}" )
    ResponseEntity<?> trelloExportInterest(@PathVariable Long interestId){
        Interest interest = interestRepository.findOne(interestId);
        interest.copy(trelloService.saveInterest(interest));
        interestRepository.save(interest);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
