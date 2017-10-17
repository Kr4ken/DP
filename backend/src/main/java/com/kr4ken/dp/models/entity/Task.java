package com.kr4ken.dp.models.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    // Id соответствующей карточки в трелло
    private String trelloId;
    // Наименование Задачи
    private String name;
    // Описание Задачи
    @Length(max = 10000)
    private String description;
    //URL изображения
    // Ссылки очень длинные
    @Length(max = 2000)
    private String img;
    // Срочность задачи
    private Boolean urgent;
    // Важность задачи
    private Boolean important;
    // Особенности поведения задачи
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    private TaskSpecial special;
    // Тип задачи
    @OneToOne
    private TaskType type;
    // Дата выполнения задачи
    private Date dueDate;
    // Чеклист с небольшими подзадачами для данной задачи
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TaskCheckList> checklists;
    // Атрибут текущей задачи
    private TaskAttribute attribute;
    // Время выполнения задачи
    private Double duration;

    Task() { // jpa only
    }

    public Task(String trelloId, String name, String description, String img, Boolean urgent, Boolean important, TaskSpecial special, TaskType type, Date dueDate, List<TaskCheckList> checklists, TaskAttribute attribute, Double duration) {
        this.trelloId = trelloId;
        this.name = name;
        this.description = description;
        this.img = img;
        this.urgent = urgent;
        this.important = important;
        this.special = special;
        this.type = type;
        this.dueDate = dueDate;
        this.checklists = checklists;
        this.attribute = attribute;
        this.duration = duration;
    }

    public Task(Task other) { // jpa only
        this.update(other);
    }

    public void update(Task other) {
        this.trelloId = other.trelloId != null ? other.trelloId : trelloId;
        this.name = other.name != null ? other.name : name;
        this.description = other.description != null ? other.description : description;
        this.img = other.img != null ? other.img : img;
        this.urgent = other.urgent != null ? other.urgent : urgent;
        this.important = other.important != null ? other.important : important;
        this.special = other.special != null ? other.special : special;
        this.type = other.type != null ? other.type : type;
        this.dueDate = other.dueDate != null ? other.dueDate : dueDate;
//        this.checklists = other.checklists != null ? other.checklists : checklists;
        if(other.checklists != null) {
            checklists.clear();
            other.checklists.forEach(taskCheckList -> taskCheckList.setTask(this));
            checklists.addAll(other.checklists);

//            this.checklists = other.checklists;
        }
//            if(checklists!=null){
//                checklists.clear();
//                checklists.addAll(other.checklists);
//            }
//            else {
//                checklists = other.checklists;
//            }
//        }
//        this.checklists = other.checklists != null ? other.checklists.stream()
//                .map(TaskCheckList::new)
//                .map(taskCheckList -> {taskCheckList.setTask(this);return taskCheckList;})
//                .collect(Collectors.toList())
//                : checklists;
        this.attribute = other.attribute != null ? other.attribute : attribute;
        this.duration = other.duration != null ? other.duration : duration;
    }


    public Task(String name, Boolean urgent, Boolean important, TaskType type) {
        this(null,
                name,
                null,
                null,
                urgent,
                important,
                null,
                type,
                null,
                null,
                null,
                null);
    }

    public Long getId() {
        return id;
    }

    public String getTrelloId() {
        return trelloId;
    }

    public void setTrelloId(String trelloId) {
        this.trelloId = trelloId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Boolean getImportant() {
        return important;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public TaskSpecial getSpecial() {
        return special;
    }

    public void setSpecial(TaskSpecial special) {
        this.special = special;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<TaskCheckList> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<TaskCheckList> checklists) {
        this.checklists = checklists;
    }

    public TaskAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(TaskAttribute attribute) {
        this.attribute = attribute;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
