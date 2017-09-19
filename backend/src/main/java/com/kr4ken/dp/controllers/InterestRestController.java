package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    @RequestMapping(method = RequestMethod.GET, value = "/{interestId}")
    public Interest readInterest(@PathVariable Long interestId) {
        return this.interestRepository.findOne(interestId);
    }

//    private void validateUser(String userId) {
////        String userId = principal.getName();
//        this.accountRepository
//                .findByUsername(userId)
//                .orElseThrow(
//                        () -> new UserNotFoundException(userId));
//    }
}