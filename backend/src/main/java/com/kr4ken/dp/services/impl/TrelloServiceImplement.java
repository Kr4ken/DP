package com.kr4ken.dp.services.impl;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrelloServiceImplement implements TrelloService{

    private final Trello trelloApi;
    private final String userName;
    private final String trelloInterestBoard;

    private List<TList> taskTypesList;
    @Autowired
    private InterestTypeRepository interestTypeRepository;


    TrelloServiceImplement ( InterestTypeRepository interestTypeRepository    ){
        this.interestTypeRepository = interestTypeRepository;
        trelloApi = new TrelloImpl("a31bb57aac7aba739505bc9975b897dd","0d98e7f23ebcefeda8a14f807def592846f9c7780872800f2d29f76e3394f684");
        userName = "user88592332";
        trelloInterestBoard = "57e04a0fda82f763f66385a1";
    }
    public List<String> getBoardsId(){
        return trelloApi.getMemberInformation(userName).getIdBoards();
    }

    public List<String> getBoardsName(){
        return trelloApi.getMemberInformation(userName).getIdBoards().stream().map((e) -> trelloApi.getBoard(e).getName()).collect(Collectors.toList());
    }

    public List<TList> getTaskTypes() {
        if(taskTypesList == null)
            taskTypesList=trelloApi.getBoard(trelloInterestBoard).fetchLists();
        return taskTypesList;
    }

    public List<Card> getTypeCards(InterestType interestType) {
        return  getTaskTypes()
                .stream()
                .filter((e) -> e.getName().equals(interestType.getName()))
                .findFirst()
                .get()
                .getCards();
    }

    private InterestType getInterestTypeFromList(TList list){
        String desc = String.format("%s.\nИз трелло\n.Id листа:%s",list.getName(),list.getId());
        return new InterestType(list.getName(),desc);
    }

    private Interest getInterestFromCard(Card card){
        String name = card.getName();
        String desc = card.getDesc();
        String ss = desc.substring(desc.indexOf('[')+1,desc.indexOf(']'));
        Integer season = Integer.getInteger(ss.substring(0,ss.indexOf('/')),0);
        Integer stage = Integer.getInteger(ss.substring(ss.indexOf('/'))+1);
        String img = card.getIdAttachmentCover();
        Optional<InterestType> ito = interestTypeRepository.findByTrelloId(card.getIdList());
        if(!ito.isPresent())return null;
        InterestType it =  ito.get();
        Integer ord = card.getPos();
        String com = card.getDesc();

        return new Interest(
                name,
                img,
                "",
                season,
                stage,
                it,
                ord,
                com,
                card.getId()
        );
    }


    @Override
    public List<InterestType> getInterestTypes() {
        return trelloApi.getBoard(trelloInterestBoard)
                .fetchLists()
                .stream()
                .map(e -> getInterestTypeFromList(e))
                .collect(Collectors.toList());
    }

    @Override
    public List<Interest> getInterests() {

        return trelloApi.getBoard(trelloInterestBoard)
                .fetchLists()
                .stream()
                .flatMap(e -> e.getCards().stream())
                .map(e -> getInterestFromCard(e))
                .collect(Collectors.toList());

    }
}
