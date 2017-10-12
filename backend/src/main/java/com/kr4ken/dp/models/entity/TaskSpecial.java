package com.kr4ken.dp.models.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TaskSpecial {
    @Id
    @GeneratedValue
    Long id;

    private TaskSpecialComplete complete;

    TaskSpecial() {
    }

    public TaskSpecial(TaskSpecial other){
        this.copy(other);
    }

    public TaskSpecial(TaskSpecialComplete complete) {
        this.complete = complete;
    }

    public void copy(TaskSpecial taskSpecial){
        this.complete = complete;
    }
    public Long getId() {
        return id;
    }

    public TaskSpecialComplete getComplete() {
        return complete;
    }
    public void setComplete(TaskSpecialComplete complete) {
        this.complete = complete;
    }

}
