package com.kr4ken.dp.models.hooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kr4ken.habitica.domain.Task;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloHook {
    private TrelloActionHook action;
    private String type;
    private Date date;

    public TrelloHook() {
    }

    public TrelloActionHook getAction() {
        return action;
    }

    public void setAction(TrelloActionHook action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
