package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.hooks.TrelloHook;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
 * Контроллер ловящий хук трелло
 **/

@RestController
@RequestMapping("/trello_hook")
public class TrelloHookController {

    TrelloHookController(){
    }

    @RequestMapping(method = RequestMethod.HEAD, value = "/test")
    ResponseEntity<?> initalHeadAnswer(){
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/test")
//    ResponseEntity<?> dropInterestTypeQuery(@RequestBody String all){
    ResponseEntity<?> cathchHook(@RequestBody TrelloHook hook){
        System.out.println(hook.getType());
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }
}
