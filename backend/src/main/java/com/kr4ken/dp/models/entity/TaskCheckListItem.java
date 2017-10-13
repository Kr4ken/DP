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

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private TaskCheckList checklist;

    TaskCheckListItem() {
    }

    public TaskCheckListItem(TaskCheckListItem other){
      this.copy(other);
    }

    public void copy(TaskCheckListItem other){
        pos = other.pos != null? other.pos:pos;
        duration = other.duration != null? other.duration:duration;
        trelloId = other.trelloId != null? other.trelloId: trelloId;
        name = other.name!= null? other.name:name;
        checked = other.checked != null? other.checked:checked;
        checklist = other.checklist!=null?other.checklist:checklist;
    }

    public TaskCheckListItem(Double pos, String name) {
        this(pos,null,null,name,null,null);
    }

    public TaskCheckListItem(Double pos, String name,TaskCheckList checklist ) {
        this(pos,null,null,name,null,checklist);
    }

    public TaskCheckListItem(Double pos, Double duration, String trelloId, String name, Boolean checked,TaskCheckList checklist) {
        this.pos = pos;
        this.duration = duration;
        this.trelloId = trelloId;
        this.name = name;
        this.checked = checked;
        this.checklist = checklist;
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

    public TaskCheckList getChecklist() {
        return checklist;
    }

    public void setChecklist(TaskCheckList checklist) {
        this.checklist = checklist;
    }
}

