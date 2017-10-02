package com.kr4ken.dp.models;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

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
    private String description;
    //URL изображения
    // Ссылки очень длинные
    @Length(max = 2000)
    private String img;
    // Категория задачи
    @Enumerated(EnumType.STRING)
    private TaskCategory category;
    // Срочность задачи
    private Boolean urgent;
    // Важность задачи
    private Boolean important;
    // Особенности поведения задачи
//    private TaskSpecial special;
    // Тип задачи
    @OneToOne
    private TaskType type;
    // Дата выполнения задачи
    private Date dueDate;
    // Чеклист с небольшими подзадачами для данной задачи
//    private TaskCheckList checkList;
    // Атрибут текущей задачи
//    private TaskAttribute attribute;
    // Время выполнения задачи
    private Double duration;

    Task() { // jpa only
    }

    public Task(String trelloId, String name, String description, String img, TaskCategory category, Boolean urgent, Boolean important, TaskSpecial special, TaskType type, Date dueDate, TaskCheckList checkList, TaskAttribute attribute, Double duration) {
        this.trelloId = trelloId;
        this.name = name;
        this.description = description;
        this.img = img;
        this.category = category;
        this.urgent = urgent;
        this.important = important;
//        this.special = special;
        this.type = type;
        this.dueDate = dueDate;
//        this.checkList = checkList;
//        this.attribute = attribute;
        this.duration = duration;
    }

    public Task(Task other) { // jpa only
//        this.copy(other);
    }

    public Task(String name, TaskCategory taskCategory, Boolean urgent, Boolean important, TaskType type) {
        this(null,
                name,
                null,
                null,
                taskCategory,
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

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
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

//    public TaskSpecial getSpecial() {
//        return special;
//    }

//    public void setSpecial(TaskSpecial special) {
//        this.special = special;
//    }

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

//    public TaskCheckList getCheckList() {
//        return checkList;
//    }
//
//    public void setCheckList(TaskCheckList checkList) {
//        this.checkList = checkList;
//    }

//    public TaskAttribute getAttribute() {
//        return attribute;
//    }

//    public void setAttribute(TaskAttribute attribute) {
//        this.attribute = attribute;
//    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
