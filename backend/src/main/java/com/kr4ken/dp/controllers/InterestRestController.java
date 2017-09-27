package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.InterestNotFoundException;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/interests")
public class InterestRestController {

    private final InterestRepository interestRepository;
    private final InterestTypeRepository interestTypeRepository;
    private final TrelloService trelloService;

    @Autowired
    InterestRestController(InterestRepository interestRepository,
                           InterestTypeRepository interestTypeRepository,
                           TrelloService trelloService){
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
        this.trelloService = trelloService;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Interest> readInterests() {
        return interestRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Interest input) {
        Interest result = interestRepository.save(new Interest(input));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{interestId}")
    ResponseEntity<?> add(@RequestBody Interest input,@PathVariable Long interestId) {
        Interest one = interestRepository.findOne(interestId);
        if(one == null){
            return new ResponseEntity(new InterestNotFoundException(interestId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        //TODO: Сделать перенос типа
//        if(one.getType() != input.getType())
//            changeType(input,input.getType());
        one.copy(input);
        interestRepository.save(one);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(one.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestId}")
    public Interest readInterest(@PathVariable Long interestId) {
        return this.interestRepository.findOne(interestId);
    }
}