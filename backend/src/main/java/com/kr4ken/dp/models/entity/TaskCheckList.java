package com.kr4ken.dp.models.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * Чеклист, который содержится в задаче
 */
@Entity
public class TaskCheckList {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String trelloId;
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TaskCheckListItem> checklistItems;
    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task task;

    TaskCheckList() {
    }

    public TaskCheckList(TaskCheckList other) {
        this.update(other);
    }

    public TaskCheckList(String name) {
        this(name, null, null, null);
    }

    public TaskCheckList(String name, Task task) {
        this(name, null, null, task);
    }

    public TaskCheckList(String name, List<TaskCheckListItem> checklistItems, Task task) {
        this(name, null, checklistItems, task);
    }

    public TaskCheckList(String name, String trelloId, List<TaskCheckListItem> checklistItems, Task task) {
        this.name = name;
        this.trelloId = trelloId;
        this.checklistItems = checklistItems;
        this.task = task;
    }

    // TODO: пока не используется, но в будующем доработать до версии Task
    public void update(TaskCheckList other) {
        this.name = other.name != null ? other.name : this.name;
        this.trelloId = other.trelloId != null ? other.trelloId : this.trelloId;
        this.checklistItems = other.checklistItems != null ? other.checklistItems :this.checklistItems;
        this.task = other.task != null ? other.task : this.task;
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

    public List<TaskCheckListItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<TaskCheckListItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public String getTrelloId() {
        return trelloId;
    }

    public void setTrelloId(String trelloId) {
        this.trelloId = trelloId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
