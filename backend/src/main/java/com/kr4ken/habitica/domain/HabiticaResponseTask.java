package com.kr4ken.habitica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
// TODO: Пока не буду использовать его ни на что кроме задач, но возможно нужно сделать универсальным для всех типов
public class HabiticaResponseTask {
    private Boolean success;
    private Task data;
    private String[] notifications;
    private Integer userV;

    public HabiticaResponseTask() {
    }

    public Boolean getSuccess() {

        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Task getData() {
        return data;
    }

    public void setData(Task data) {
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

