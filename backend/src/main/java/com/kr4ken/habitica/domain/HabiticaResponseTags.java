package com.kr4ken.habitica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
// TODO: Пока не буду использовать его ни на что кроме задач, но возможно нужно сделать универсальным для всех типов
public class HabiticaResponseTags {
    private Boolean success;
    private List<Tag> data;
    private String[] notifications;
    private Integer userV;

    public HabiticaResponseTags() {
    }

    public Boolean getSuccess() {

        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Tag> getData() {
        return data;
    }

    public void setData(List<Tag> data) {
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

