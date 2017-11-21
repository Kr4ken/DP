package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.hooks.HabiticaHook;
import com.kr4ken.dp.services.intf.DivineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер отвечающий за обработку хуков с хабитики
 */

@RestController
@RequestMapping("/habitica_hook")
public class HabiticaHookController {
    private final DivineService divineService;

    @Autowired
    HabiticaHookController(DivineService divineService) {
        this.divineService = divineService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/test")
    ResponseEntity<?> catchHook(@RequestBody HabiticaHook hook) {
        System.out.println("Catch habitica hook");
        System.out.println(hook.getType() + " : " + hook.getTask().getText());
        if (hook.getDirection().equals("up"))
            divineService.scoreTaskFromHabitica(hook.getTask().getAlias());
        //TODO: Сделать вариант когда down
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }
}
