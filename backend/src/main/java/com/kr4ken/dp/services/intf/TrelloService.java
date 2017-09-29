package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.Interest;

import java.util.List;

public interface TrelloService {
    // Получение
    List<InterestType> getInterestTypes();
    List<Interest> getInterests();
    Interest getInterest(Interest interest);
    InterestType getInterestType(InterestType interestType);
    // Сохранение
    InterestType saveInterestType(InterestType intrestType);
    Interest saveInterest(Interest intrest);

    // Удаление
    InterestType deleteInterestType(InterestType intrestType);
    Interest deleteInterest(Interest interest);

    Interest chooseNewInterest(InterestType interestType);

    void testDeleteAttachment(Interest interest);
}
