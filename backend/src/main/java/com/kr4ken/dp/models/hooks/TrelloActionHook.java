package com.kr4ken.dp.models.hooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Метаданные о действиях которые приходят в хуке трелло
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloActionHook {
    private String id;
    private String idMemberCreator;
    private TrelloActionDataHook data;
    private String type;
    private Date date;

    public TrelloActionHook() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdMemberCreator() {
        return idMemberCreator;
    }

    public void setIdMemberCreator(String idMemberCreator) {
        this.idMemberCreator = idMemberCreator;
    }

    public TrelloActionDataHook getData() {
        return data;
    }

    public void setData(TrelloActionDataHook data) {
        this.data = data;
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

