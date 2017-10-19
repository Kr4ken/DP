package com.kr4ken.dp.models.hooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloActionHook {
    private String id;
    private String idMemberCreator;
    private TrelloActionDataHook data;

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
}
