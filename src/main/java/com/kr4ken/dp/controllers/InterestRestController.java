package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.UserNotFoundException;
import com.kr4ken.dp.models.AccountRepository;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.resources.InterestResource;
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
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/{userId}/interests")
public class InterestRestController {

    private final InterestRepository interestRepository;

    private final AccountRepository accountRepository;

    @Autowired
    InterestRestController(InterestRepository interestRepository,
                           AccountRepository accountRepository) {
        this.interestRepository = interestRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Resources<InterestResource> readBookmarks(Principal principal) {
        this.validateUser(principal);

        List<InterestResource> bookmarkResourceList = interestRepository
                .findByAccountUsername(principal.getName()).stream()
                .map(InterestResource::new)
                .collect(Collectors.toList());

        return new Resources<>(bookmarkResourceList);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(Principal principal, @RequestBody Interest input) {
        this.validateUser(principal);

        return accountRepository
                .findByUsername(principal.getName())
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

                    Link forOneInterest = new InterestResource(result).getLink(Link.REL_SELF);

                    return ResponseEntity.created(URI
                            .create(forOneInterest.getHref()))
                            .build();
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestId}")
    public InterestResource readInterest(Principal principal, @PathVariable Long interestId) {
        this.validateUser(principal);
        return new InterestResource(
                this.interestRepository.findOne(interestId));
    }

    private void validateUser(Principal principal) {
        String userId = principal.getName();
        this.accountRepository
                .findByUsername(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(userId));
    }
}