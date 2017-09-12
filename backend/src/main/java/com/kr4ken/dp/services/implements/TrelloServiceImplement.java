package com.kr4ken.dp.services;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.services.interfaces.TrelloService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrelloServiceImplement implements TrelloService{

    private final Trello trelloApi;
    private final String userName;
    private final String trelloInterestBoard;

    private List<TList> taskTypesList;

    TrelloServiceImplement (){
        trelloApi = new TrelloImpl("a31bb57aac7aba739505bc9975b897dd","0d98e7f23ebcefeda8a14f807def592846f9c7780872800f2d29f76e3394f684");
        userName = "user88592332";
        trelloInterestBoard = "57e04a0fda82f763f66385a1";
    }

    @Override
    public List<String> getBoardsId(){
        return trelloApi.getMemberInformation(userName).getIdBoards();
    }

    @Override
    public List<String> getBoardsName(){
        return trelloApi.getMemberInformation(userName).getIdBoards().stream().map((e) -> trelloApi.getBoard(e).getName()).collect(Collectors.toList());
    }

    @Override
    public List<TList> getTaskTypes() {
        if(taskTypesList == null)
            taskTypesList=trelloApi.getBoard(trelloInterestBoard).fetchLists();
        return taskTypesList;
    }

    @Override
    public List<Card> getTypeCards(InterestType interestType) {
        return  getTaskTypes()
                .stream()
                .filter((e) -> e.getName().equals(interestType.getName()))
                .findFirst()
                .get()
                .getCards();
    }
}
