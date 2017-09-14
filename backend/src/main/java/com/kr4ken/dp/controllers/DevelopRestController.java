package com.kr4ken.dp.controllers;

import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/develop")
public class DevelopRestController {
    private final TrelloService trelloService;

    DevelopRestController(TrelloService trelloService) {
        this.trelloService = trelloService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/boards")
    List<String> readInterests() {
        return null;
//        return trelloService.getBoardsName();
    }

}
