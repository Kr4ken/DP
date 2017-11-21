package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.entity.Interest;
import com.kr4ken.dp.models.repository.InterestRepository;
import com.kr4ken.dp.models.repository.InterestTypeRepository;
import com.kr4ken.dp.services.intf.DivineService;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер который отвечает за логику работы с системой
 */

@RestController
@RequestMapping("/divine")
public class DivineController {
    private final InterestRepository interestRepository;
    private final InterestTypeRepository interestTypeRepository;
    private final TrelloService trelloService;
    private final DivineService divineService;

    DivineController(InterestRepository interestRepository,
                     InterestTypeRepository interestTypeRepository,
                     TrelloService trelloService,
                     DivineService divineService) {
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
        this.trelloService = trelloService;
        this.divineService = divineService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/mix/{interestTypeId}")
    ResponseEntity<?> trelloMix(@PathVariable Long interestTypeId) {
        Interest result = divineService.mixInterests(interestTypeId);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/complete/{interestTypeId}")
    ResponseEntity<?> completeInterestTypeQuery(@PathVariable Long interestTypeId) {
        Interest result = divineService.completeInterests(interestTypeId);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/refer/{interestTypeId}")
    ResponseEntity<?> referInterestTypeQuery(@PathVariable Long interestTypeId) {
        Interest result = divineService.referInterests(interestTypeId);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/drop/{interestTypeId}")
    ResponseEntity<?> dropInterestTypeQuery(@PathVariable Long interestTypeId) {
        Interest result = divineService.dropInterests(interestTypeId);
        return ResponseEntity.ok(result);
    }

}
