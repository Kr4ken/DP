package com.kr4ken.dp.models.resources;

import com.kr4ken.dp.controllers.InterestRestController;
import com.kr4ken.dp.controllers.InterestTypeRestController;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestType;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class InterestTypeResource extends ResourceSupport  {

    private final InterestType interestType;

    public InterestTypeResource(InterestType interestType){
        this.interestType = interestType;
        this.add(linkTo(InterestTypeRestController.class).withRel("interestTypes"));
        this.add(linkTo(methodOn(InterestTypeRestController.class)
                .readInterestType(interestType.getId())).withSelfRel());
    }

    public InterestType getInterestType(){
        return interestType;
    }
}
