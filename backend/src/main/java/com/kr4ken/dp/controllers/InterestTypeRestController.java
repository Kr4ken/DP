package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.InterestTypeNotFoundException;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/interestTypes")
public class InterestTypeRestController {

    private final InterestTypeRepository interestTypeRepository;
    private final TrelloService trelloService;

    @Autowired
    InterestTypeRestController(InterestTypeRepository interestTypeRepository,
                               TrelloService trelloService) {
        this.interestTypeRepository = interestTypeRepository;
        this.trelloService = trelloService;
    }

    private void trelloSync(InterestType interestType){
        interestTypeRepository.save(trelloService.saveInterestType(interestType));
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<InterestType> readInterestTypes() {
        return interestTypeRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody InterestType input, @RequestParam(required = false) Optional<Boolean> trello) {
        InterestType result = interestTypeRepository.save(new InterestType(input));
        if(trello.isPresent() && trello.get()){
            trelloSync(result);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{interestTypeId}")
    ResponseEntity<?> update(@PathVariable Long interestTypeId,@RequestBody InterestType input, @RequestParam(required = false) Optional<Boolean> trello) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType == null) {
            return new ResponseEntity(new InterestTypeNotFoundException(interestTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        interestType.copy(input);

        interestTypeRepository.save(interestType);
        if(trello.isPresent() && trello.get()){
            trelloSync(interestType);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(interestType.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{interestTypeId}")
    ResponseEntity<?> delete(@PathVariable Long interestTypeId, @RequestParam(required = false) Optional<Boolean> trello) {

        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType == null) {
            return new ResponseEntity(new InterestTypeNotFoundException(interestTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        if(trello.isPresent() && trello.get()) {
            trelloService.deleteInterestType(interestType);
        }
        interestTypeRepository.delete(interestTypeId);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestTypeId}")
    public InterestType readInterestType(@PathVariable Long interestTypeId) {
        return this.interestTypeRepository.findOne(interestTypeId);
    }
}
