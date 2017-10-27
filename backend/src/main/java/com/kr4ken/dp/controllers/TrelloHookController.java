package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.Task;
import com.kr4ken.dp.models.hooks.TrelloHook;
import com.kr4ken.dp.models.repository.TaskRepository;
import com.kr4ken.dp.services.intf.DivineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/*
 * Контроллер ловящий хук трелло
 **/

@RestController
@RequestMapping("/trello_hook")
public class TrelloHookController {

    private final DivineService divineService;
    private final TaskRepository taskRepository;

    @Autowired
    TrelloHookController(DivineService divineService,
                          TaskRepository taskRepository) {
        this.divineService = divineService;
        this.taskRepository = taskRepository;
    }

    @RequestMapping(method = RequestMethod.HEAD, value = "/test")
    ResponseEntity<?> initalHeadAnswer() {
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/test")
//    ResponseEntity<?> dropInterestTypeQuery(@RequestBody String all){
    ResponseEntity<?> cathchHook(@RequestBody TrelloHook hook) {
        System.out.println(hook.getAction().getType());
        // Пофиксить для определенных типов
        if (hook.getAction().getData().getCard() != null) {
            Optional<Task> task = taskRepository.findByTrelloId(hook.getAction().getData().getCard().getId());
            if (task.isPresent())
                divineService.updateFromTrello(task.get());
        }
//        System.out.println(hook.getT);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }
}
