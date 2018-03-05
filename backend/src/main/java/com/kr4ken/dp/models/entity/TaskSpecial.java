package com.kr4ken.dp.models.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Особенности поведения задачи
 * которые должны учитываться при работе с ней
 */
@Entity
public class TaskSpecial {
    @Id
    @GeneratedValue
    Long id;

    private TaskSpecialComplete complete;

    TaskSpecial() {
    }

    public TaskSpecial(TaskSpecial other){
        this.update(other);
    }

    public TaskSpecial(TaskSpecialComplete complete) {
        this.complete = complete;
    }

    public void update(TaskSpecial taskSpecial){
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
