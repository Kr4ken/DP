package com.kr4ken.dp.controllers;

import com.kr4ken.dp.config.DivineConfig;
import com.kr4ken.dp.exceptions.InterestTypeNotFoundException;
import com.kr4ken.dp.models.entity.InterestType;
import com.kr4ken.dp.models.repository.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

/**
 * Контроллер предоставляющий рестфул интерфейс
 * Для объектов InterestType - Типов интересов
 */

@RestController
@RequestMapping("/interestTypes")
public class InterestTypeRestController {

    private final InterestTypeRepository interestTypeRepository;
    private final TrelloService trelloService;

    private final DivineConfig divineConfig;

    @Autowired
    InterestTypeRestController(InterestTypeRepository interestTypeRepository,
                               DivineConfig divineConfig,
                               TrelloService trelloService) {
        this.interestTypeRepository = interestTypeRepository;
        this.trelloService = trelloService;
        this.divineConfig = divineConfig;
    }

    private void trelloSync(InterestType interestType, Optional<Boolean> trello) {
        Boolean sync = trello.isPresent() ? trello.get() : divineConfig.getTrelloSync();
        if (sync)
            interestTypeRepository.save(trelloService.sync(interestType));
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<InterestType> readInterestTypes() {
        return interestTypeRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody InterestType input, @RequestParam(required = false) Optional<Boolean> trello) {
        InterestType result = interestTypeRepository.save(new InterestType(input));
        trelloSync(result, trello);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{interestTypeId}")
    ResponseEntity<?> update(@PathVariable Long interestTypeId, @RequestBody InterestType input, @RequestParam(required = false) Optional<Boolean> trello) {
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType == null) {
            return new ResponseEntity(new InterestTypeNotFoundException(interestTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }
        interestType.update(input);
        interestTypeRepository.save(interestType);
        trelloSync(interestType, trello);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(interestType.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{interestTypeId}")
    ResponseEntity<?> delete(@PathVariable Long interestTypeId, @RequestParam(required = false) Optional<Boolean> trello) {

        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        if (interestType == null) {
            return new ResponseEntity(new InterestTypeNotFoundException(interestTypeId.toString()),
                    HttpStatus.NOT_FOUND);
        }

        Boolean sync = trello.isPresent() ? trello.get() : divineConfig.getTrelloSync();
        if (sync) {
            trelloService.delete(interestType);
        }
        interestTypeRepository.delete(interestTypeId);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{interestTypeId}")
    public InterestType readInterestType(@PathVariable Long interestTypeId) {
        return this.interestTypeRepository.findOne(interestTypeId);
    }
}
