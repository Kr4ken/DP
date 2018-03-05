package com.kr4ken.habitica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
// TODO: Пока не буду использовать его ни на что кроме задач, но возможно нужно сделать универсальным для всех типов
public class HabiticaResponseTag {
    private Boolean success;
    private Tag data;
    private String[] notifications;
    private Integer userV;

    public HabiticaResponseTag() {
    }

    public Boolean getSuccess() {

        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Tag getData() {
        return data;
    }

    public void setData(Tag data) {
        this.data = data;
    }

    public String[] getNotifications() {
        return notifications;
    }

    public void setNotifications(String[] notifications) {
        this.notifications = notifications;
    }

    public Integer getUserV() {
        return userV;
    }

    public void setUserV(Integer userV) {
        this.userV = userV;
    }
}

