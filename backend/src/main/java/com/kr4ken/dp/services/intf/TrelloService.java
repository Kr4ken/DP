package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.Interest;

import java.util.List;

public interface TrelloService {
    List<InterestType> getInterestTypes();
    List<Interest> getInterests();
    Interest getInterest(Interest interest);
    InterestType getInterestType(InterestType interestType);
    InterestType saveInterestType(InterestType intrestType);
    InterestType deleteInterestType(InterestType intrestType);
    Interest saveInterest(Interest intrest);
    Interest chooseNewInterest(InterestType interestType);

    void testDeleteAttachment(Interest interest);
}
