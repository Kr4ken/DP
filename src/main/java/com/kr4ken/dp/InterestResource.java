package com.kr4ken.dp;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class InterestResource extends ResourceSupport {

    private final Interest interest;

    public InterestResource(Interest interest){
        String username = interest.getAccount().getUsername();
        this.interest = interest;
        this.add(linkTo(InterestRestController.class,username).withRel("interests"));
        this.add(linkTo(methodOn(InterestRestController.class,username)
                        .readInterest(username, interest.getId())).withSelfRel());
    }

    public Interest getInterest(){
        return interest;
    }
}
