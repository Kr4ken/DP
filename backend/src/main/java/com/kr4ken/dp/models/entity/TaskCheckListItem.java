package com.kr4ken.dp.models.entity;


import javax.persistence.*;

@Entity
public class TaskCheckListItem {
    @Id
    @GeneratedValue
    private Long id;

    private Double pos;
    private Double duration;
    private String trelloId;
    private String name;
    private Boolean checked;

    TaskCheckListItem() {
    }

    public TaskCheckListItem(TaskCheckListItem other){
      this.copy(other);
    }

    public void copy(TaskCheckListItem other){
        pos = other.pos;
        duration = other.duration;
        trelloId = other.trelloId;
        name = other.name;
        checked = other.checked;
    }

    public TaskCheckListItem(Double pos, String name) {
        this(pos,null,null,name,null);
    }

    public TaskCheckListItem(Double pos, Double duration, String trelloId, String name, Boolean checked) {
        this.pos = pos;
        this.duration = duration;
        this.trelloId = trelloId;
        this.name = name;
        this.checked = checked;
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Double getPos() {
        return pos;
    }

    public void setPos(Double pos) {
        this.pos = pos;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getTrelloId() {
        return trelloId;
    }

    public void setTrelloId(String trelloId) {
        this.trelloId = trelloId;
    }
}

