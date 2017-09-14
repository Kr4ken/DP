package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.models.resources.InterestResource;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/interests")
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

//    @RequestMapping(method = RequestMethod.GET, value = "/trellosync")
//    Resources<String> trelloTaskSync(){
//    }



    @RequestMapping(method = RequestMethod.GET)
    Resources<InterestResource> readInterests() {
        List<InterestResource> interestResourceList = interestRepository
                .findAll()
                .stream()
                .map(InterestResource::new)
                .collect(Collectors.toList());
        return new Resources<>(interestResourceList);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Interest input) {
         Interest result = interestRepository.save(new Interest(
                            input.name,
                            input.img,
                            input.source,
                            input.season,
                            input.stage,
                            input.type,
                            input.ord,
                            input.comment,
                            input.trelloId
         ));

         Link forOneInterest = new InterestResource(result).getLink(Link.REL_SELF);

         return ResponseEntity.created(URI.create(forOneInterest.getHref()))
                            .build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestId}")
    public InterestResource readInterest(@PathVariable Long interestId) {
        return new InterestResource(
                this.interestRepository.findOne(interestId));
    }

//    private void validateUser(String userId) {
////        String userId = principal.getName();
//        this.accountRepository
//                .findByUsername(userId)
//                .orElseThrow(
//                        () -> new UserNotFoundException(userId));
//    }
}