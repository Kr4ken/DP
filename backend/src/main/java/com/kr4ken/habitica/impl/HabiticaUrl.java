package com.kr4ken.habitica.impl;

import com.kr4ken.habitica.Habitica;
import com.kr4ken.habitica.domain.Argument;

public class HabiticaUrl {
    public static final String API_URL = "https://habitica.com/api/v3";

    // Tasks
    public static final String GET_USER_TASKS = "/tasks/user?";
    public static final String GET_TASK = "/tasks/{taskId}?";
    public static final String CREATE_USER_TASK = "/tasks/user?";
    public static final String UPDATE_TASK = "/tasks/{taskId}?";
    public static final String DELETE_TASK = "/tasks/{taskId}?";

    // Tags
    public static final String GET_USER_TAGS = "/tags?";
    public static final String GET_TAG = "/tags/{tagId}?";
    public static final String CREATE_TAG = "/tags?";
    public static final String UPDATE_TAG = "/tags/{tagId}?";
    public static final String DELETE_TAG = "/tags/{tagId}?";




    private String baseUrl;
    private Argument[] args = {};

    private HabiticaUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static HabiticaUrl createUrl(String baseUrl){
        return new HabiticaUrl(baseUrl);
    }

    public HabiticaUrl params(Argument... args){
        this.args = args;
        return this;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder(API_URL);
        builder.append(baseUrl);
        for(Argument arg : args){
            builder.append("&");
            builder.append(arg.getArgName());
            builder.append("=");
            builder.append(arg.getArgValue());
        }
        return builder.toString();
    }
}