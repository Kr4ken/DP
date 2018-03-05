package com.kr4ken.dp.models.entity;


import javax.persistence.*;

/**
 * Тип задачи
 */
@Entity
public class TaskType {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    //Id соответствующего списка в трелло
    private String trelloId;

    TaskType(){}; // jpa only

    public TaskType(TaskType other) {
        this.update(other);
    }

    // Обновление данных из непустых данных другого типа
    public void update(TaskType other){
        this.name = other.name == null?this.name:other.name;
        this.description = other.description == null?this.description:other.description;
        this.trelloId = other.trelloId == null?this.trelloId:other.trelloId;
    }

    public TaskType(String name, String description, String trelloId) {
        this.name = name;
        this.description = description;
        this.trelloId = trelloId;
    }

    public Long getId() {
        return id;
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

    public String getTrelloId() {
        return trelloId;
    }

    public void setTrelloId(String trelloId) {
        this.trelloId = trelloId;
    }
}
