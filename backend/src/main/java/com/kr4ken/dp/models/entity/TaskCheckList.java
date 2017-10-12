package com.kr4ken.dp.models.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class TaskCheckList {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String trelloId;
    @OneToMany(orphanRemoval=true)
    private List<TaskCheckListItem> checklistItems;

    TaskCheckList() {
    }

    public TaskCheckList(TaskCheckList other) {
        this.copy(other);
    }

    public TaskCheckList(String name) {
        this(name, null, null);
    }

    public TaskCheckList(String name, List<TaskCheckListItem> checklistItems) {
        this(name, null, checklistItems);
    }

    public TaskCheckList(String name, String trelloId, List<TaskCheckListItem> checklistItems) {
        this.name = name;
        this.trelloId = trelloId;
        this.checklistItems = checklistItems;
    }

    public void copy(TaskCheckList other) {
        name = other.name;
        checklistItems = other.checklistItems;
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
}
