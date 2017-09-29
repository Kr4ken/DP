package com.kr4ken.dp.controllers;

import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/*
 * Контроллер который отвечает за логику работы с системой
 **/

@RestController
@RequestMapping("/divine")
public class DivineRestController {
    private final  InterestRepository interestRepository;
    private final InterestTypeRepository interestTypeRepository;
    private final TrelloService trelloService;

    private final String COMPLETE_TYPE = "Закончено";
    private final String REFER_TYPE = "Отложено";

    DivineRestController(InterestRepository interestRepository,
                         InterestTypeRepository interestTypeRepository,
                         TrelloService trelloService) {
        this.interestRepository = interestRepository;
        this.interestTypeRepository = interestTypeRepository;
        this.trelloService = trelloService;
    }


    // Интересы
    // Перемещает интерес в другую группу в самый верх списка
    private void changeType(Interest interest, InterestType interestType){
        Collection<Interest> interests = interestRepository.findByTypeOrderByOrd(interestType);
        interest.setOrd(
                interests.toArray(new Interest[]{})[0].getOrd()-1
        );
        interest.setType(interestType);
    }

    Interest mixInterestType(Long interestTypeId){
        Random random  = new Random();
        InterestType interestType = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(interestType);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        int max = interestArray.length -1,min = 0;
        Integer randomInterest = random.nextInt(max - min + 1) + min;
        changeType(interestArray[randomInterest],interestType);
        interestRepository.save(interestArray[randomInterest]);
        return interestArray[randomInterest];
    }

    @RequestMapping(method = RequestMethod.POST, value = "/mix/{interestTypeId}")
    ResponseEntity<?> trelloMix(@PathVariable Long interestTypeId, @RequestParam Optional<Boolean> trello){
        Interest result = mixInterestType(interestTypeId);
        if(trello.isPresent() && trello.get()){
           interestRepository.save(trelloService.saveInterest(result));
        }
        return ResponseEntity.ok(result);
    }

    Interest completeInterestType(Long interestTypeId){
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        InterestType itTarget = interestTypeRepository.findByName(COMPLETE_TYPE).get();
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        changeType(interestArray[0],itTarget);
        interestRepository.save(interestArray[0]);
        return interestArray[0];
    }


    @RequestMapping(method = RequestMethod.POST, value = "/complete/{interestTypeId}")
    ResponseEntity<?> completeInterestTypeQuery(@PathVariable Long interestTypeId,@RequestParam Optional<Boolean> trello){
        Interest complete =  completeInterestType(interestTypeId);
        Interest result = mixInterestType(interestTypeId);
        if(trello.isPresent() && trello.get()){
            interestRepository.save(trelloService.saveInterest(complete));
            interestRepository.save(trelloService.saveInterest(result));
        }
        return ResponseEntity.ok(result);
    }

    Interest referInterestType(Long interestTypeId){
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        InterestType itTarget = interestTypeRepository.findByName(REFER_TYPE).get();
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        changeType(interestArray[0],itTarget);
        interestRepository.save(interestArray[0]);
        return interestArray[0];
    }

    @RequestMapping(method = RequestMethod.POST, value = "/refer/{interestTypeId}")
    ResponseEntity<?> referInterestTypeQuery(@PathVariable Long interestTypeId,@RequestParam Optional<Boolean> trello){
        Interest refer =  referInterestType(interestTypeId);
        Interest result = mixInterestType(interestTypeId);
        if(trello.isPresent() && trello.get()){
            interestRepository.save(trelloService.saveInterest(refer));
            interestRepository.save(trelloService.saveInterest(result));
        }
        return ResponseEntity.ok(result);
    }

    Interest dropInterestType(Long interestTypeId){
        InterestType it = interestTypeRepository.findOne(interestTypeId);
        Collection<Interest> interests =  interestRepository.findByTypeOrderByOrd(it);
        Interest[] interestArray = interests.toArray(new Interest[]{});
        interestRepository.delete(interestArray[0]);
        return interestArray[0];
    }

    @RequestMapping(method = RequestMethod.POST, value = "/drop/{interestTypeId}")
    ResponseEntity<?> dropInterestTypeQuery(@PathVariable Long interestTypeId,@RequestParam Optional<Boolean> trello){
        Interest drop = dropInterestType(interestTypeId);
        Interest result = mixInterestType(interestTypeId);
        if(trello.isPresent() && trello.get()){
            trelloService.saveInterest(drop);
            interestRepository.save(trelloService.saveInterest(result));
        }
        return ResponseEntity.ok(result);
    }

}
