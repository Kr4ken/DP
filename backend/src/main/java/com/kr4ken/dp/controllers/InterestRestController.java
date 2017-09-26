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

    Interest mixInterestType(Long interestTypeId){
        Random random  = new Random();
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        int max = interestArray.length -1,min = 0;
        Integer randomInterest = random.nextInt(max - min + 1) + min;
        changeType(interestArray[randomInterest],it);
        interestRepository.save(interestArray[0]);
        return interestArray[0];
    }

   @RequestMapping(method = RequestMethod.POST, value = "/mix/{interestTypeId}")
   ResponseEntity<?> mixInterestTypeQuery(@PathVariable Long interestTypeId){
        mixInterestType(interestTypeId);
       return ResponseEntity.ok(HttpEntity.EMPTY);
   }

    @RequestMapping(method = RequestMethod.POST, value = "/mix/{interestTypeId}/trelloexport")
    ResponseEntity<?> mixInterestTypeTrello(@PathVariable Long interestTypeId){
        trelloService.saveInterest( mixInterestType(interestTypeId));
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    Interest completeInterestType(Long interestTypeId){
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        InterestType itTarget = interestTypeRepository.findByName("Закончено").get();
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        changeType(interestArray[0],itTarget);
        interestRepository.save(interestArray[0]);
        return interestArray[0];
    }


    @RequestMapping(method = RequestMethod.POST, value = "/complete/{interestTypeId}")
    ResponseEntity<?> completeInterestTypeQuery(@PathVariable Long interestTypeId){
        completeInterestType(interestTypeId);
        mixInterestType(interestTypeId);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/complete/{interestTypeId}/trelloexport")
    ResponseEntity<?> completeInterestTypeTrello(@PathVariable Long interestTypeId){
        trelloService.saveInterest( completeInterestType(interestTypeId));
        trelloService.saveInterest( mixInterestType(interestTypeId));
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    Interest referInterestType(Long interestTypeId){
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        InterestType itTarget = interestTypeRepository.findByName("Отложено").get();
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        changeType(interestArray[0],itTarget);
        interestRepository.save(interestArray[0]);
        return interestArray[0];
    }

    @RequestMapping(method = RequestMethod.POST, value = "/refer/{interestTypeId}")
    ResponseEntity<?> referInterestTypeQuery(@PathVariable Long interestTypeId){
        referInterestType(interestTypeId);
        mixInterestType(interestTypeId);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/refer/{interestTypeId}/trelloexport")
    ResponseEntity<?> referInterestTypeTrello(@PathVariable Long interestTypeId){
        trelloService.saveInterest( referInterestType(interestTypeId));
        trelloService.saveInterest( mixInterestType(interestTypeId));
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    // Работает только если тип уже есть в БД
    @RequestMapping(method = RequestMethod.POST, value = "/trelloimport")
    ResponseEntity<?> trelloTaskImport(){
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

    ResponseEntity<?> moveInterest(Interest interest,Double position){
        interestRepository
                .findAll()
                .sort(Comparator.comparing(Interest::getOrd));
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.PUT, value ="/{interestId}/trelloexport" )
    ResponseEntity<?> trelloTaskTypeExport(@PathVariable Long interestId){
        Interest one = interestRepository.findOne(interestId);
        if(one != null) {
           one = trelloService.saveInterest(one);
           interestRepository.save(one);
        }
        else {
            return new ResponseEntity(new InterestNotFoundException(interestId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Interest> readInterests() {
        return interestRepository.findAll();
    }

    private void changeType(Interest interest, InterestType interestType){
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(interestType);
        interest.setOrd(
        interests.toArray(new Interest[]{})[0].getOrd()-1
        );
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
        if(one.getType() != input.getType())
            changeType(input,input.getType());
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