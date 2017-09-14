package com.kr4ken.dp.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.julienvey.trello.domain.TList;

import javax.persistence.*;

@Entity
public class InterestType {

    @Id
    @GeneratedValue
    private Long id;

    InterestType() { // jpa only
    }

    public void copy(InterestType other){
        this.name = other.name == null?this.name:other.name;
        this.trelloId = other.trelloId == null?this.trelloId:other.trelloId;
        this.description = other.description == null?this.description:other.description;
    }

    public InterestType(String name,String description) {
        this(name,description,null);
    }

    public InterestType(String name,String description,String trelloId) {
        this.name = name;
        this.description = description;
        this.trelloId = trelloId;
    }

    //Id соответствующего списка в трелло
    public String trelloId;
    public String name;
    public String description;

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrelloId() {
        return trelloId;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
