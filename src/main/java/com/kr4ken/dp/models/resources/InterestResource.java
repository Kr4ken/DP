package com.kr4ken.dp.models.resources;

import com.kr4ken.dp.controllers.InterestRestController;
import com.kr4ken.dp.models.Interest;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class InterestResource extends ResourceSupport {

    private final Interest interest;

    public InterestResource(Interest interest){
        String username = interest.getAccount().getUsername();
        this.interest = interest;
        this.add(linkTo(InterestRestController.class,username).withRel("interests"));
        this.add(linkTo(methodOn(InterestRestController.class,username)
                        .readInterest(null, interest.getId())).withSelfRel());
    }

    public Interest getInterest(){
        return interest;
    }
}
