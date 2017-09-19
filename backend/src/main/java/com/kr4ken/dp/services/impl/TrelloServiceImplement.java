package com.kr4ken.dp.services.impl;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Attachment;
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
public class TrelloServiceImplement implements TrelloService {

    private final Trello trelloApi;
    private final String userName;
    private final String trelloInterestBoard;

    private List<TList> taskTypesList;
    @Autowired
    private InterestTypeRepository interestTypeRepository;


    TrelloServiceImplement(InterestTypeRepository interestTypeRepository) {
        this.interestTypeRepository = interestTypeRepository;
        trelloApi = new TrelloImpl("a31bb57aac7aba739505bc9975b897dd", "0d98e7f23ebcefeda8a14f807def592846f9c7780872800f2d29f76e3394f684");
        userName = "user88592332";
        trelloInterestBoard = "57e04a0fda82f763f66385a1";
    }

    public static int parseInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<String> getBoardsId() {
        return trelloApi.getMemberInformation(userName).getIdBoards();
    }

    public List<String> getBoardsName() {
        return trelloApi.getMemberInformation(userName).getIdBoards().stream().map((e) -> trelloApi.getBoard(e).getName()).collect(Collectors.toList());
    }

    public List<TList> getTaskTypes() {
        if (taskTypesList == null)
            taskTypesList = trelloApi.getBoard(trelloInterestBoard).fetchLists();
        return taskTypesList;
    }

    public List<Card> getTypeCards(InterestType interestType) {
        return getTaskTypes()
                .stream()
                .filter((e) -> e.getName().equals(interestType.getName()))
                .findFirst()
                .get()
                .getCards();
    }

    private InterestType getInterestTypeFromList(TList list) {
        String desc = String.format("%s.\nИз трелло\n.Id листа:%s", list.getName(), list.getId());
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
        if (card.getIdAttachmentCover() != null) {
            img = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover()).getUrl();
        }
        Optional<InterestType> ito = interestTypeRepository.findByTrelloId(card.getIdList());
        if (!ito.isPresent()) return null;
        InterestType it = ito.get();
        Integer ord = card.getPos();
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
                .map(e -> getInterestTypeFromList(e))
                .collect(Collectors.toList());
    }

    @Override
    public List<Interest> getInterests() {
        return trelloApi.getBoard(trelloInterestBoard)
                .fetchCards()
                .stream()
                .map(this::getInterestFromCard)
                .filter(e -> e != null)
                .collect(Collectors.toList());
    }


    @Override
    public void saveInterestType(InterestType interestType) {
        if (interestType.getTrelloId() == null) {
            // Если листа до этого не было
            // То создать его
        } else {
            TList list = trelloApi.getList(interestType.getTrelloId());
            if (interestType.getName() != null)
                list.setName(interestType.getName());
            trelloApi.updateList(list);
        }
    }

    @Override
    public void saveInterest(Interest interest) {
        if (interest.trelloId == null) {
            // Если листа до этого не было
            // То создать его
        } else {
            Card card = trelloApi.getCard(interest.trelloId);
            String description = "";

            if (interest.getName() != null)
                card.setName(interest.getName());
            if (interest.getDescription() != null)
                description = interest.getDescription();
            if (interest.getImg() != null) {
                //Уже есть аттачмент
                if (card.getIdAttachmentCover() != null) {
                    Attachment attachment = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover());
                    attachment.setUrl(interest.getImg());
                    //TODO:Работу с аттачментами
                } else {
                    //TODO:Создать новый аттачмент
                }
            }
            // Если есть серийность
            if(interest.getSeason() != 0) {
               description+=" [" + interest.getStage().toString()    + "/"+ interest.getSeason()+"]";
            }
            if(interest.getType() != null && !interest.getType().getTrelloId().equals(card.getIdList())) {
                //TODO:Переместить карточку в другой лист
            }
            //TODO: Разобраться с позицией
            }

        }
    }
