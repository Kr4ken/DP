package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.services.intf.HabiticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/habitica")
public class HabiticaRestController {

    private final HabiticaService habiticaService;


    @Autowired
    HabiticaRestController(HabiticaService habiticaService
    ) {
        this.habiticaService = habiticaService;
    }

    @RequestMapping(value = "/tasks",method = RequestMethod.GET)
    Collection<Task> readInterests() {
        return habiticaService.getTasks();
    }

}
