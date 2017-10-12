package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.InterestNotFoundException;
import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.repository.InterestRepository;
import com.kr4ken.dp.models.repository.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    private void trelloSync(Interest interest){
        interestRepository.save(trelloService.saveInterest(interest));
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Interest> readInterests(@RequestParam Optional<Long> type,@RequestParam Optional<Boolean> sorted) {
        if(sorted.isPresent() && sorted.get())
            if (type.isPresent())
                return interestRepository.findByTypeOrderByOrd(interestTypeRepository.findOne(type.get()));
            else
                return interestRepository.findAllByOrderByOrd();
        else
            if (type.isPresent())
                return interestRepository.findByType(interestTypeRepository.findOne(type.get()));
            else
                return interestRepository.findAll();


    }

    @RequestMapping(method = RequestMethod.GET,value = "/current")
    List<Interest> readCurrentInterests() {
        return interestTypeRepository.findAll()
                .stream()
                .map(interestRepository::findFirstByTypeOrderByOrd)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Interest input, @RequestParam(required = false) Optional<Boolean> trello ) {
        Interest result = interestRepository.save(new Interest(input));
        if(trello.isPresent() && trello.get()){
            trelloSync(result);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{interestId}")
    ResponseEntity<?> add(@RequestBody Interest input,@PathVariable Long interestId, @RequestParam(required = false) Optional<Boolean> trello) {
        Interest one = interestRepository.findOne(interestId);
        if(one == null){
            return new ResponseEntity(new InterestNotFoundException(interestId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        one.copy(input);
        interestRepository.save(one);
        if(trello.isPresent() && trello.get()) {
           trelloSync(one);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(one.getId()).toUri();
        return ResponseEntity.created(location).build();
    }


    @RequestMapping(method = RequestMethod.DELETE,value = "/{interestId}")
    ResponseEntity<?> delete(@PathVariable Long interestId, @RequestParam(required = false) Optional<Boolean> trello) {

        Interest interest = interestRepository.findOne(interestId);
        if (interest == null) {
            return new ResponseEntity(new InterestNotFoundException(interestId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        if(trello.isPresent() && trello.get()) {
            trelloService.deleteInterest(interest);
        }
        interestTypeRepository.delete(interestId);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }



    @RequestMapping(method = RequestMethod.GET, value = "/{interestId}")
    public Interest readInterest(@PathVariable Long interestId) {
        return this.interestRepository.findOne(interestId);
    }
}