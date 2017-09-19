package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/develop")
public class DevelopRestController {
    private final  InterestRepository interestRepository;
    private final InterestTypeRepository interestTypeRepository;

    DevelopRestController(InterestRepository interestRepository,
                          InterestTypeRepository interestTypeRepository) {
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getSortedList")
    Collection<Interest> readInterests() {
        return interestRepository.findByTypeOrderByOrd(interestTypeRepository.findOne(1L));
//        return trelloService.getBoardsName();
    }

}
