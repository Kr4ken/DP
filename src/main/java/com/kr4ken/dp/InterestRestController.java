package com.kr4ken.dp;

import org.apache.catalina.util.ResourceSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{userId}/interests")
public class InterestRestController {
    private final InterestRepository interestRepository;
    private final InterestTypeRepository interestTypeRepository;
    private final AccountRepository accountRepository;

    @Autowired
    InterestRestController(InterestRepository interestRepository,
                           InterestTypeRepository interestTypeRepository,
                           AccountRepository accountRepository
                           ) {
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Resources<InterestResource> readInterests(@PathVariable String userId) {
        this.validateUser(userId);
        List<InterestResource> interestResourceList =  interestRepository
                                .findByAccountUsername(userId).stream().map(InterestResource::new)
                                .collect(Collectors.toList());
        return new Resources<>(interestResourceList);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String userId, @RequestBody Interest input) {
        this.validateUser(userId);

        return this.accountRepository
                .findByUsername(userId)
                .map(account -> {
                    Interest result = interestRepository.save(new Interest(account,
                            input.name,
                            input.img,
                            input.source,
                            input.season,
                            input.stage,
                            input.type,
                            input.ord,
                            input.comment
                    ));

                    Link forOneInterest = new InterestResource(result).getLink("self");

                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(result.getId()).toUri();

                    return ResponseEntity.created(URI.create(forOneInterest.getHref())).build();
                })
                .orElse(ResponseEntity.noContent().build());

    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestId}")
    InterestResource readInterest(@PathVariable String userId, @PathVariable Long interestId) {
        this.validateUser(userId);
        return new InterestResource(this.interestRepository.findOne(interestId));
    }

    private void validateUser(String userId) {
        this.accountRepository.findByUsername(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}
