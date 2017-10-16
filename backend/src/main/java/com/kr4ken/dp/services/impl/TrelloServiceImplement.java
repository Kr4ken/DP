package com.kr4ken.dp.services.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.config.TrelloConfig;
import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.TrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrelloServiceImplement implements TrelloService {

    private final Trello trelloApi;
    private final String userName;
    private final String trelloInterestBoard;
    private final String trelloProgressBoard;
    private final TrelloConfig trelloConfig;

    private final HashMap<String,String> trelloLists;

    private final String trelloUrgentLabel;
    private final String trelloNUrgentLabel;

    private final String trelloImportantLabel;
    private final String trelloNImportantLabel;

    private InterestTypeRepository interestTypeRepository;
    private InterestRepository interestRepository;
    private TaskTypeRepository taskTypeRepository;
    private TaskRepository taskRepository;
    private TaskSpecialRepository taskSpecialRepository;
    private TaskCheckListRepository taskCheckListRepository;
    private TaskCheckListItemRepository taskCheckListItemRepository;


    @Autowired
    TrelloServiceImplement(InterestTypeRepository interestTypeRepository,
                           InterestRepository interestRepository,
                           TaskTypeRepository taskTypeRepository,
                           TaskRepository taskRepository,
                           TaskSpecialRepository taskSpecialRepository,
                           TaskCheckListRepository taskCheckListRepository,
                           TaskCheckListItemRepository taskCheckListItemRepository,
                           TrelloConfig trelloConfig) {
        this.interestTypeRepository = interestTypeRepository;
        this.interestRepository = interestRepository;
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
        this.taskSpecialRepository = taskSpecialRepository;
        this.taskCheckListRepository = taskCheckListRepository;
        this.taskCheckListItemRepository = taskCheckListItemRepository;
        this.trelloConfig = trelloConfig;
        // Подгрузка конфигурации
        trelloApi = new TrelloImpl(trelloConfig.getApplicationKey(),trelloConfig.getAccessToken());
        userName = trelloConfig.getUser();
        trelloInterestBoard = trelloConfig.getInterestBoard();
        trelloProgressBoard = trelloConfig.getProgressBoard();
        trelloLists = trelloConfig.getIdNameMap();

        trelloUrgentLabel = trelloConfig.getUrgentTaskLabel();
        trelloNUrgentLabel = trelloConfig.getNurgentTaskLabel();
        trelloImportantLabel = trelloConfig.getImportantTaskLabel();
        trelloNImportantLabel = trelloConfig.getNimportantTaskLabel();
    }

    private static int parseInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Double parseDouble(String string, Double defaultValue) {
        try {
            return Double.parseDouble(string);
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

    @Override
    public Interest deleteInterest(Interest interest){
        Card card = trelloApi.getCard(interest.getTrelloId());
        if(card != null){
            trelloApi.deleteCard(interest.getTrelloId());
        }
        return interest;
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
    public Interest getInterest(Interest interest){
        Card card = trelloApi.getCard(interest.getTrelloId());
        return getInterestFromCard(card);
    };

    @Override
    public InterestType getInterestType(InterestType interestType){
        TList tList = trelloApi.getList(interestType.getTrelloId());
        return getInterestTypeFromList(tList);
    };

    // Дальше начинаются таски

    private TaskType getTaskTypeFromList(TList list) {
        String desc = String.format("%s.Id листа:%s", list.getName(), list.getId());
        return new TaskType(list.getName(), desc, list.getId());
    }

    @Override
    public TaskType getTaskType(TaskType taskType) {
        TList tList = trelloApi.getList(taskType.getTrelloId());
        return getTaskTypeFromList(tList);
    }

    @Override
    public List<TaskType> getTaskTypes() {
        return trelloApi.getBoard(trelloProgressBoard)
                .fetchLists()
                .stream()
                .map(this::getTaskTypeFromList)
                .collect(Collectors.toList());
    }

    @Override
    public TaskType saveTaskType(TaskType taskType) {
        if (taskType.getTrelloId() == null) {
            // Если листа до этого не было
            // То создать его
            TList list = new TList();
            list.setName(taskType.getName());
            list.setIdBoard(trelloProgressBoard);
            taskType.setTrelloId(trelloApi.createList(list).getId());
        } else {
            TList list = trelloApi.getList(taskType.getTrelloId());
            if (taskType.getName() != null)
                list.setName(taskType.getName());
            trelloApi.updateList(list);
        }
        return taskType;
    }

    @Override
    public TaskType deleteTaskType(TaskType taskType) {
        TList list = trelloApi.getList(taskType.getTrelloId());
        if (list != null) {
            list.setClosed(true);
            trelloApi.updateList(list);
        }
        return taskType;
    }

    private List<TaskCheckList> getChecklistsFromCard(Card card){
        List<TaskCheckList> result = null;
        for(String checklistId:card.getIdChecklists()){
            if(result ==null) result = new ArrayList<>();
           CheckList checkList = trelloApi.getCheckList(checklistId);
           TaskCheckList taskCheckList = new TaskCheckList(checkList.getName());
           taskCheckList.setTrelloId(checklistId);
           ArrayList<TaskCheckListItem> items = null;
           for(CheckItem checkitem:checkList.getCheckItems()){
               if(items == null) items = new ArrayList<>();
               String itemName = checkitem.getName();
               Double duration = itemName.contains("[")? parseDouble(itemName.substring(itemName.indexOf("[")+1,itemName.indexOf("]")),0.):0.;
               itemName = itemName.contains("[")?itemName.substring(0,itemName.indexOf("[")):itemName;
               TaskCheckListItem taskCheckListItem = new TaskCheckListItem(checkitem.getPos(),duration,checkitem.getId(),itemName,checkitem.getState().equals("complete"),taskCheckList);
               items.add(taskCheckListItem);
           }
           taskCheckList.setChecklistItems(items);
           result.add(taskCheckList);
        }
        return result;
    }

    private TaskSpecial getSpecialFromCard(Card card){
        String desc = card.getDesc();
        String special = desc.contains("[special](")?desc.substring(desc.indexOf("[special]("),desc.indexOf(")",desc.indexOf("[special]("))):"";
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        TaskSpecial taskSpecial = null;
        try {
            taskSpecial = objectMapper.readValue(special,TaskSpecial.class);
        }catch (IOException e){
            // TODO: ошибку
        }
        return taskSpecial;
    }


    private Task getTaskFromCard(Card card) {
        // Наименование задачи
        String name = card.getName();
        // Длительность задачи
        Double duration =name.contains("[")?parseDouble(name.substring(name.indexOf("[")+1,name.indexOf("]")),0.):0.;
        name = name.contains("[")?name.substring(0,name.indexOf("[")):name;
        // Описание задачи
        String desc =card.getDesc().contains("[special]")?card.getDesc().substring(0,card.getDesc().indexOf("[special]")):card.getDesc();
        // Загрузка обложки, если есть
        String img = null;
        if (card.getIdAttachmentCover() != null && !card.getIdAttachmentCover().isEmpty()) {
            img = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover()).getUrl();
        }
        // Срочность
        Boolean urgent = card.getLabels().stream().anyMatch(e -> e.getId().equals(trelloUrgentLabel));
        // Важность
        Boolean important = card.getLabels().stream().anyMatch(e -> e.getId().equals(trelloImportantLabel));

        // Особенности
        TaskSpecial special = getSpecialFromCard(card);

        // Тип задачи
        Optional<TaskType> taskTypeOptional = taskTypeRepository.findByTrelloId(card.getIdList());
        TaskType taskType;
        if(taskTypeOptional.isPresent())
            taskType = taskTypeOptional.get();
        else
            return null;

        Task result = new Task(name,urgent,important,taskType);
        result.setDuration(duration);
        result.setDescription(desc);
        result.setImg(img);
        result.setSpecial(special);

        // Дата выполнения задачи
        Date due = card.getDue();

        result.setDueDate(due);

        // Чеклисты
        List<TaskCheckList> checkLists = getChecklistsFromCard(card);
        // TODO: проставить для каждого чеклиста задачу
        if(checkLists != null)
            checkLists.forEach(taskCheckList -> taskCheckList.setTask(result));
        result.setChecklists(checkLists);

        // Атрибут
        TaskAttribute attribute = TaskAttribute.Int;
        result.setAttribute(attribute);

        return result;
//        return new Task(
//                card.getId(),
//                name,
//                desc,
//                img,
//                urgent,
//                important,
//                special,
//                taskType,
//                due,
//                checkLists,
//                attribute,
//                duration
//        );
    }

    @Override
    public List<Task> getTasks() {
        return trelloApi.getBoard(trelloProgressBoard)
                .fetchCards()
                .stream()
                .map(this::getTaskFromCard)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public Task getTask(Task taks) {
        return null;
    }


    @Override
    public Task saveTask(Task task) {
        return null;
    }

    @Override
    public Task deleteTask(Task task) {
        return null;
    }
}
