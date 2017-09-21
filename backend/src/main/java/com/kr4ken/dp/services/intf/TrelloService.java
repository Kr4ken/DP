package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.Interest;

import java.util.List;

public interface TrelloService {
    public List<InterestType> getInterestTypes();
    public List<Interest> getInterests();
    public InterestType saveInterestType(InterestType intrestType);
    public InterestType deleteInterestType(InterestType intrestType);
    public Interest saveInterest(Interest intrest);
    public Interest chooseNewInterest(InterestType interestType);

    public void testDeleteAttachment(Interest interest);
}
