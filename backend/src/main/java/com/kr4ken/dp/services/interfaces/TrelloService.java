package com.kr4ken.dp.services.interfaces;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.kr4ken.dp.models.InterestType;

import java.util.List;

public interface TrelloService {
    public List<String> getBoardsId();
    public List<String> getBoardsName();
    public List<TList> getTaskTypes();
    public List<Card> getTypeCards(InterestType interestType);
}
