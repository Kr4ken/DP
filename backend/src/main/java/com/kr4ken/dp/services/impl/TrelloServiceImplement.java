package com.kr4ken.dp.services.impl;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Attachment;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.models.Interest;
import com.kr4ken.dp.models.InterestRepository;
import com.kr4ken.dp.models.InterestType;
import com.kr4ken.dp.models.InterestTypeRepository;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrelloServiceImplement implements TrelloService {

    private final Trello trelloApi;
    private final String userName;
    private final String trelloInterestBoard;

    private List<TList> taskTypesList;
    @Autowired
    private InterestTypeRepository interestTypeRepository;
    @Autowired
    private InterestRepository interestRepository;


    TrelloServiceImplement(InterestTypeRepository interestTypeRepository,
                           InterestRepository interestRepository) {
        this.interestTypeRepository = interestTypeRepository;
        this.interestRepository = interestRepository;
        trelloApi = new TrelloImpl("a31bb57aac7aba739505bc9975b897dd", "0d98e7f23ebcefeda8a14f807def592846f9c7780872800f2d29f76e3394f684");
        userName = "user88592332";
        trelloInterestBoard = "57e04a0fda82f763f66385a1";
    }

    private static int parseInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private InterestType getInterestTypeFromList(TList list) {
        String desc = String.format("%s.Id листа:%s", list.getName(), list.getId());
        return new InterestType(list.getName(), desc, list.getId());
    }

    private Interest getInterestFromCard(Card card) {
        String name = card.getName();
        String desc = card.getDesc();
        Integer season = 0;
        Integer stage = 0;
        String ss = "";
        if (desc.indexOf('[') >= 0) {
            ss = desc.substring(desc.indexOf('[') + 1, desc.indexOf(']'));
            stage = parseInteger(ss.substring(0, ss.indexOf('/')), 0);
            season = parseInteger(ss.substring(ss.indexOf('/') + 1), 0);
        }
        // Загрузка обложки, если есть
        String img = null;
        if (card.getIdAttachmentCover() != null && !card.getIdAttachmentCover().isEmpty()) {
            img = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover()).getUrl();
        }
        Optional<InterestType> ito = interestTypeRepository.findByTrelloId(card.getIdList());
        if (!ito.isPresent()) return null;
        InterestType it = ito.get();
        Double ord = card.getPos();
        String com;
        if (!ss.isEmpty()) {
            com = card.getDesc().replaceAll(ss, "");
        } else {
            com = card.getDesc();
        }

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
                .map(this::getInterestTypeFromList)
                .collect(Collectors.toList());
    }

    @Override
    public List<Interest> getInterests() {
        return trelloApi.getBoard(trelloInterestBoard)
                .fetchCards()
                .stream()
                .map(this::getInterestFromCard)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public InterestType saveInterestType(InterestType interestType) {
        if (interestType.getTrelloId() == null) {
            // Если листа до этого не было
            // То создать его
            TList list = new TList();
            list.setName(interestType.getName());
            list.setIdBoard(trelloInterestBoard);
            interestType.setTrelloId(trelloApi.createList(list).getId());
        } else {
            TList list = trelloApi.getList(interestType.getTrelloId());
            if (interestType.getName() != null)
                list.setName(interestType.getName());
            trelloApi.updateList(list);
        }
        return interestType;
    }

    @Override
    public InterestType deleteInterestType(InterestType interestType) {
        TList list = trelloApi.getList(interestType.getTrelloId());
        if (list != null) {
            list.setClosed(true);
            trelloApi.updateList(list);
        }
        return interestType;
    }

    private Attachment createCoverAttachment(String cardId,String imgUrl){
        Attachment new_attach = new Attachment();
        new_attach.setUrl(imgUrl);
        new_attach.setName("Обложка");
        new_attach = trelloApi.addAttachmentToCard(cardId,new_attach);
        return new_attach;
    }

    @Override
    public Interest saveInterest(Interest interest) {
        Card card = interest.getTrelloId() == null ? new Card() : trelloApi.getCard(interest.getTrelloId());
        String description = "";

        if (interest.getName() != null)
            card.setName(interest.getName());
        if (interest.getDescription() != null)
            description = interest.getDescription();
        if (interest.getImg() != null) {
            //Уже есть аттачмент
            if (card.getIdAttachmentCover() != null) {
                Attachment attachment = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover());
                // Если не совпадает с изображением - Удаляем
                if (!attachment.getUrl().equals(interest.getImg())) {
                    trelloApi.deleteAttachment(card.getId(),attachment.getId());
                    // И создаем новый
                    Attachment new_attach = createCoverAttachment(card.getId(),interest.getImg());
                    card.setIdAttachmentCover(new_attach.getId());
                    interest.setImg(new_attach.getUrl());
                }
            } else {
                // Если нет аттачмента просто создаем новый
                Attachment new_attach = createCoverAttachment(card.getId(),interest.getImg());
                card.setIdAttachmentCover(new_attach.getId());
                interest.setImg(new_attach.getUrl());
            }
        }
        // Если есть серийность
        if (interest.getSeason() != 0) {
            description += " [" + interest.getStage().toString() + "/" + interest.getSeason() + "]";
        }
        if (interest.getType() != null && !interest.getType().getTrelloId().equals(card.getIdList())) {
            card.setIdList(interest.getType().getTrelloId());
        }

        if(interest.getOrd() != null){
            card.setPos(interest.getOrd());
        }

        if(!description.isEmpty())
            card.setDesc(description);

        if(interest.getTrelloId() == null){
            card = trelloApi.createCard(interest.getType().getTrelloId(),card);
            interest.setTrelloId(card.getId());
        }
        else {
            trelloApi.updateCard(card);
        }

        return interest;
    }


    public Interest chooseNewInterest(InterestType interestType) {
        return new Interest(null);
    }

    @Override
    public void testDeleteAttachment(Interest interest){
        Card card = trelloApi.getCard(interest.getTrelloId());
       trelloApi.deleteAttachment(interest.getTrelloId(),card.getIdAttachmentCover());
    }
}
