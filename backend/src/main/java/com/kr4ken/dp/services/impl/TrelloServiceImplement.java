package com.kr4ken.dp.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import com.kr4ken.dp.config.TrelloConfig;
import com.kr4ken.dp.models.entity.*;
import com.kr4ken.dp.models.repository.*;
import com.kr4ken.dp.services.intf.TrelloService;
import com.sun.org.apache.xpath.internal.operations.Bool;
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

    private final HashMap<String, String> trelloLists;

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
        trelloApi = new TrelloImpl(trelloConfig.getApplicationKey(), trelloConfig.getAccessToken());
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
    public Interest deleteInterest(Interest interest) {
        Card card = trelloApi.getCard(interest.getTrelloId());
        if (card != null) {
            trelloApi.deleteCard(interest.getTrelloId());
        }
        return interest;
    }

    private Attachment createCoverAttachment(String cardId, String imgUrl) {
        Attachment new_attach = new Attachment();
        new_attach.setUrl(imgUrl);
        new_attach.setName("Обложка");
        new_attach = trelloApi.addAttachmentToCard(cardId, new_attach);
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
                    trelloApi.deleteAttachment(card.getId(), attachment.getId());
                    // И создаем новый
                    Attachment new_attach = createCoverAttachment(card.getId(), interest.getImg());
                    card.setIdAttachmentCover(new_attach.getId());
                    interest.setImg(new_attach.getUrl());
                }
            } else {
                // Если нет аттачмента просто создаем новый
                Attachment new_attach = createCoverAttachment(card.getId(), interest.getImg());
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

        if (interest.getOrd() != null) {
            card.setPos(interest.getOrd());
        }

        if (!description.isEmpty())
            card.setDesc(description);

        if (interest.getTrelloId() == null) {
            card = trelloApi.createCard(interest.getType().getTrelloId(), card);
            interest.setTrelloId(card.getId());
        } else {
            trelloApi.updateCard(card);
        }

        return interest;
    }

    @Override
    public Interest getInterest(Interest interest) {
        Card card = trelloApi.getCard(interest.getTrelloId());
        return getInterestFromCard(card);
    }

    ;

    @Override
    public InterestType getInterestType(InterestType interestType) {
        TList tList = trelloApi.getList(interestType.getTrelloId());
        return getInterestTypeFromList(tList);
    }

    ;

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

    private Double getDuration(String str) {
        return str.contains("[") ? parseDouble(str.substring(str.indexOf("[") + 1, str.indexOf("]")), 0.) : 0.;
    }

    private String getNameWithoutDuration(String str) {
        return str.contains("[") ? str.substring(0, str.indexOf("[")) : str;
    }


    private List<TaskCheckList> getChecklistsFromCard(Card card) {
        List<TaskCheckList> result = null;
        for (String checklistId : card.getIdChecklists()) {
            if (result == null) result = new ArrayList<>();
            CheckList checkList = trelloApi.getCheckList(checklistId);
            TaskCheckList taskCheckList = new TaskCheckList(checkList.getName());
            taskCheckList.setTrelloId(checklistId);
            ArrayList<TaskCheckListItem> items = null;
            for (CheckItem checkitem : checkList.getCheckItems()) {
                if (items == null) items = new ArrayList<>();
                String itemName = checkitem.getName();
                Double duration = getDuration(itemName);
                itemName = getNameWithoutDuration(itemName);
                TaskCheckListItem taskCheckListItem = new TaskCheckListItem(checkitem.getPos(), duration, checkitem.getId(), itemName, checkitem.getState().equals("complete"), taskCheckList);
                items.add(taskCheckListItem);
            }
            taskCheckList.setChecklistItems(items);
            result.add(taskCheckList);
        }
        return result;
    }

    private TaskSpecial getSpecialFromCard(Card card) {
        String desc = card.getDesc();
        String specialPrefix = "[special](";
        String special = desc.contains(specialPrefix) ? desc.substring(desc.indexOf(specialPrefix) + specialPrefix.length(), desc.indexOf(")", desc.indexOf(specialPrefix))) : "";
        ObjectMapper objectMapper = new ObjectMapper();
        TaskSpecial taskSpecial = null;
        try {
            taskSpecial = objectMapper.readValue(special, TaskSpecial.class);
        } catch (IOException e) {
            // TODO: ошибку
        }
        return taskSpecial;
    }

    private TaskAttribute getCardAtribute(Card card) {
        List<Label> labels = card.getLabels();
        if (labels.stream().anyMatch(label -> label.getId().equals(trelloConfig.getStrTaskLabel()))) {
            return TaskAttribute.Str;
        }
        if (labels.stream().anyMatch(label -> label.getId().equals(trelloConfig.getIntTaskLabel()))) {
            return TaskAttribute.Int;
        }
        if (labels.stream().anyMatch(label -> label.getId().equals(trelloConfig.getConTaskLabel()))) {
            return TaskAttribute.Con;
        }
        if (labels.stream().anyMatch(label -> label.getId().equals(trelloConfig.getPerTaskLabel()))) {
            return TaskAttribute.Per;
        }
        return TaskAttribute.Str;
    }


    private Task getTaskFromCard(Card card) {
        Task result = new Task();
        //trelloID
        result.setTrelloId(card.getId());

        // Описание задачи
        String desc = card.getDesc().contains("[special]") ? card.getDesc().substring(0, card.getDesc().indexOf("[special]")) : card.getDesc();
        result.setDescription(desc);

        // Загрузка обложки, если есть
        String img = null;
        if (card.getIdAttachmentCover() != null && !card.getIdAttachmentCover().isEmpty()) {
            img = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover()).getUrl();
        }
        result.setImg(img);

        // Срочность
        Boolean urgent = card.getLabels().stream().anyMatch(e -> e.getId().equals(trelloUrgentLabel));
        result.setUrgent(urgent);

        // Важность
        Boolean important = card.getLabels().stream().anyMatch(e -> e.getId().equals(trelloImportantLabel));
        result.setImportant(important);

        // Особенности
        result.setSpecial(getSpecialFromCard(card));

        // Тип задачи
        // Если имеет неизвестный тип, то возвращаем null
        Optional<TaskType> taskTypeOptional = taskTypeRepository.findByTrelloId(card.getIdList());
        TaskType taskType;
        if (taskTypeOptional.isPresent())
            taskType = taskTypeOptional.get();
        else
            return null;
        result.setType(taskType);
        // Выполнить до
        Date due = card.getDue();
        result.setDueDate(due);
        // Чеклисты
        List<TaskCheckList> checkLists = getChecklistsFromCard(card);
        if (checkLists != null)
            checkLists.forEach(taskCheckList -> taskCheckList.setTask(result));
        result.setChecklists(checkLists);

        // Аттрибут
        TaskAttribute attribute = getCardAtribute(card);
        result.setAttribute(attribute);


        String name = card.getName();
        // Длительность задачи
        Double duration = name.contains("[") ? parseDouble(name.substring(name.indexOf("[") + 1, name.indexOf("]")), 0.) : 0.;
        result.setDuration(duration);
        // Наименование задачи
        name = name.contains("[") ? name.substring(0, name.indexOf("[")) : name;
        result.setName(name);

        return result;
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
    public Task getTask(Task task) {
        return getTaskFromCard(trelloApi.getCard(task.getTrelloId()));
    }

    private Boolean mergeCheckListItems(CheckList checkList, TaskCheckList taskCheckList) {
        Boolean dirty = false;
        List<CheckItem> trelloItems = checkList.getCheckItems();
        List<TaskCheckListItem> divineItems = taskCheckList.getChecklistItems();
        Set<CheckItem> notUsedCheckItems = new HashSet<>(trelloItems);
        // Проходим по новым элементам
        for (TaskCheckListItem divineItem : divineItems) {
            Boolean exitst = false;
            // Проходим по старым элементам
            for (CheckItem trelloItem : trelloItems) {
                // Если элемент уже есть в старом чеклисте, то обновляем его
                if (trelloItem.getId().equals(divineItem.getTrelloId())) {
                    exitst = true;
                    notUsedCheckItems.remove(trelloItem);
                    if (mergeCheckListItem(divineItem, trelloItem)) {
                        dirty = true;
                        trelloApi.updateCheckItem(checkList.getIdCard(), trelloItem);
                    }
                }
            }
            // Если элемента нет среди старых элементов, то добавляем его
            if (!exitst) {
                dirty = true;
                CheckItem newItem = new CheckItem();
                mergeCheckListItem(divineItem, newItem);
                newItem = trelloApi.createCheckItem(checkList.getId(), newItem);
                divineItem.setTrelloId(newItem.getId());
            }
        }
        // Удаляем все старые элементы, которых нет в новом
        for (CheckItem rem : notUsedCheckItems) {
            dirty = true;
            trelloApi.deleteCheckItem(checkList.getId(), rem.getId());
        }
        return dirty;
    }

    private Boolean mergeCheckLists(Card trelloCard, Task divineTask) {
        Boolean dirty = false;
        List<CheckList> trelloList = trelloCard.getIdChecklists().stream().map(e -> trelloApi.getCheckList(e)).collect(Collectors.toList());
        List<TaskCheckList> divineList = divineTask.getChecklists();
        Set<CheckList> notUsedCheckList = new HashSet<>(trelloList);
        // Проходим по новым элементам
        for (TaskCheckList divine : divineList) {
            Boolean exitst = false;
            // Проходим по старым элементам
            for (CheckList trello : trelloList) {
                // Если элемент уже есть в старом чеклисте, то обновляем его
                if (trello.getId().equals(divine.getTrelloId())) {
                    exitst = true;
                    notUsedCheckList.remove(trello);
                    if(mergeCheckList(divine,trello)){
                        dirty = true;
                        trelloApi.updateCheckList(trello.getId(),trello);
                    }
                }
            }
            // Если элемента нет среди старых элементов, то добавляем его
            if (!exitst) {
                dirty = true;
                CheckList newCheckList = new CheckList();
                mergeCheckList(divine,newCheckList);
                newCheckList.setName(divine.getName());
                newCheckList.setIdCard(trelloCard.getId());
                trelloApi.createCheckList(newCheckList.getIdCard(), newCheckList);
                divine.setTrelloId(newCheckList.getId());
                for (TaskCheckListItem divineItem : divine.getChecklistItems()) {
                    CheckItem newItem = new CheckItem();
                    newItem.setName(divineItem.getName() + "[" + divineItem.getDuration() + "]");
                    newItem.setState(divineItem.getChecked() ? "complete" : "incomplete");
                    newItem.setPos(divineItem.getPos());

                    newItem = trelloApi.createCheckItem(newCheckList.getId(), newItem);
                    divineItem.setTrelloId(newItem.getId());
                }
            }
        }
        for (CheckList rem : notUsedCheckList) {
            dirty = true;
            trelloApi.deleteCheckList(rem.getId());
        }
        return dirty;
    }


    private Boolean mergeCheckList(TaskCheckList divine, CheckList trello) {
        Boolean dirty = false;
        String newName =divine.getName();
        dirty = !trello.getName().equals(newName);
        trello.setName(divine.getName());
        dirty = mergeCheckListItems(trello, divine);
        return dirty;
    }

    private Boolean mergeCheckListItem(TaskCheckListItem divineItem, CheckItem trelloItem) {
        Boolean dirty = false;
        String newName = divineItem.getName() + "[" + divineItem.getDuration() + "]";
        String newState = divineItem.getChecked() ? "complete" : "incomplete";
        Double newPos = divineItem.getPos();

        dirty = !trelloItem.getName().equals(newName) || !trelloItem.getState().equals(newState) || !newPos.equals(trelloItem.getPos());
        trelloItem.setName(newName);
        trelloItem.setState(newState);
        trelloItem.setPos(newPos);
        return dirty;
    }

    @Override
    public Task saveTask(Task task) {
        Card card = task.getTrelloId() != null ? trelloApi.getCard(task.getTrelloId()) : new Card();
        // Срок задачи
        if (task.getDueDate() != null)
            card.setDue(task.getDueDate());

        // Описание задачи
        String description = "";
        if (task.getDescription() != null)
            description = task.getDescription();
        // Особенности задачи
        if (task.getSpecial() != null) {
            ObjectMapper mapper = new ObjectMapper();
            String special;
            try {
                special = mapper.writeValueAsString(task.getSpecial());
            } catch (Exception e) {
                special = "";
            }
            description += " [special](" + special + ")";
        }
        card.setDesc(description);

        // Чек-листы
        if (task.getChecklists() != null) {
            //Проверить работоспособность
            mergeCheckLists(card, task);
        }
        // Лист Карточки
        card.setIdList(task.getType().getTrelloId());

        // Обложка карточки
        if (task.getImg() != null) {
            //Уже есть аттачмент
            if (card.getIdAttachmentCover() != null) {
                Attachment attachment = trelloApi.getCardAttachment(card.getId(), card.getIdAttachmentCover());
                // Если не совпадает с изображением - Удаляем
                if (!attachment.getUrl().equals(task.getImg())) {
                    trelloApi.deleteAttachment(card.getId(), attachment.getId());
                    // И создаем новый
                    Attachment new_attach = createCoverAttachment(card.getId(), task.getImg());
                    card.setIdAttachmentCover(new_attach.getId());
                    task.setImg(new_attach.getUrl());
                }
            } else {
                // Если нет аттачмента просто создаем новый
                Attachment new_attach = createCoverAttachment(card.getId(), task.getImg());
                card.setIdAttachmentCover(new_attach.getId());
                task.setImg(new_attach.getUrl());
            }
        }
        List<String> newLabelsId = new ArrayList<>();
        // Важность
        if (task.getImportant())
            newLabelsId.add(trelloConfig.getImportantTaskLabel());
        else
            newLabelsId.add(trelloConfig.getNimportantTaskLabel());
        // Срочность
        if (task.getUrgent())
            newLabelsId.add(trelloConfig.getUrgentTaskLabel());
        else
            newLabelsId.add(trelloConfig.getNurgentTaskLabel());
        // Атрибут
        switch (task.getAttribute()) {
            case Str:
                newLabelsId.add(trelloConfig.getStrTaskLabel());
                break;
            case Con:
                newLabelsId.add(trelloConfig.getConTaskLabel());
                break;
            case Per:
                newLabelsId.add(trelloConfig.getPerTaskLabel());
                break;
            case Int:
                newLabelsId.add(trelloConfig.getIntTaskLabel());
                break;
            default:
                newLabelsId.add(trelloConfig.getStrTaskLabel());
        }
        card.setIdLabels(newLabelsId);
        // Наименование карточки
        if (task.getName() != null)
            card.setName(task.getName() + " [" + task.getDuration() + "]");

        // Сохраняем карточку и обновляем исходный таск
        if (task.getTrelloId() == null) {
            card = trelloApi.createCard(task.getType().getTrelloId(), card);
            task.setTrelloId(card.getId());
        } else {
            trelloApi.updateCard(card);
        }
        return task;
    }

    @Override
    public Task deleteTask(Task task) {
        Card card = trelloApi.getCard(task.getTrelloId());
        if (card != null) {
            trelloApi.deleteCard(task.getTrelloId());
        }
        return task;
    }

    @Override
    public List<TaskType> getActiveList() {
        String input = trelloConfig.getInputTaskList();
        String distr = trelloConfig.getDistributeTaskList();
        String pause = trelloConfig.getPauseTaskList();
        String complete = trelloConfig.getCompleteTaskList();
        return taskTypeRepository.findAll().stream().filter(taskType -> !taskType.getTrelloId().equals(input) && !taskType.getTrelloId().equals(distr) && !taskType.getTrelloId().equals(pause) && !taskType.getTrelloId().equals(complete)).collect(Collectors.toList());
    }

    @Override
    public TaskType getHabitType() {
        Optional<TaskType> result = taskTypeRepository.findByTrelloId(trelloConfig.getHabitTaskList());
        if (result.isPresent())
            return result.get();
        return null;
    }

    @Override
    public TaskType getDailyType() {
        Optional<TaskType> result = taskTypeRepository.findByTrelloId(trelloConfig.getDailyTaskList());
        if (result.isPresent())
            return result.get();
        return null;
    }

    @Override
    public TaskType getCompleteType() {
        Optional<TaskType> result = taskTypeRepository.findByTrelloId(trelloConfig.getCompleteTaskList());
        if (result.isPresent())
            return result.get();
        return null;
    }
}
