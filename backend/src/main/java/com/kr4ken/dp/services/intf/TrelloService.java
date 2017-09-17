package com.kr4ken.dp.services.intf;

import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.Interest;

import java.util.List;

public interface TrelloService {
    public List<InterestType> getInterestTypes();
    public List<Interest> getInterests();
    public void saveInterestType(InterestType intrestType);
    public void saveInterest(Interest intrest);
}
