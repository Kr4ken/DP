package com.kr4ken.dp.controllers;

import com.kr4ken.dp.exceptions.InterestTypeNotFoundException;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.models.resources.InterestTypeResource;
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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/interestTypes")
public class InterestTypeRestController {

    private final InterestTypeRepository interestTypeRepository;

    @Autowired
    InterestTypeRestController(InterestTypeRepository interestTypeRepository) {
        this.interestTypeRepository = interestTypeRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Resources<InterestTypeResource> readInterestTypes() {
        List<InterestTypeResource> interestTypeResourceList = interestTypeRepository
                .findAll()
                .stream()
                .map(InterestTypeResource::new)
                .collect(Collectors.toList());
        return new Resources<>(interestTypeResourceList);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody InterestType input) {
        InterestType result = interestTypeRepository.save(new InterestType(
                input.name,
                input.description
        ));
        Link forOneInterest = new InterestTypeResource(result).getLink(Link.REL_SELF);
        return ResponseEntity.created(URI.create(forOneInterest.getHref()))
                .build();
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{interestTypeId}")
    ResponseEntity<?> update(@PathVariable Long interestTypeId,@RequestBody InterestType input) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType == null) {
            return new ResponseEntity(new InterestTypeNotFoundException(interestTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        interestType.setName(input.getName());
        interestType.setDescription(input.getDescription());

        interestTypeRepository.save(interestType);
        Link forOneInterest = new InterestTypeResource(interestType).getLink(Link.REL_SELF);
        return ResponseEntity.ok(URI.create(forOneInterest.getHref()));
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{interestTypeId}")
    ResponseEntity<?> delete(@PathVariable Long interestTypeId) {

        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType == null) {
            return new ResponseEntity(new InterestTypeNotFoundException(interestTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        interestTypeRepository.delete(interestTypeId);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestTypeId}")
    public InterestTypeResource readInterestType(@PathVariable Long interestTypeId) {
        return new InterestTypeResource(
                this.interestTypeRepository.findOne(interestTypeId));
    }
}
