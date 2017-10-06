package com.kr4ken.dp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.HashMap;

@Configuration
@PropertySource("classpath:trello.properties")
public class TrelloConfig {

    @Value("${applicationKey}")
    private String applicationKey;
    @Value("${accessToken}")
    private String accessToken;
    @Value("${user}")
    private String user;
    @Value("${app.interestBoard}")
    private String interestBoard;
    @Value("${app.progressBoard}")
    private String progressBoard;

    @Value("${app.task.complete}")
    private String completeTaskList;
    @Value("${app.task.input}")
    private String inputTaskList;
    @Value("${app.task.distribute}")
    private String distributeTaskList;
    @Value("${app.task.pause}")
    private String pauseTaskList;
    @Value("${app.task.habit}")
    private String habitTaskList;
    @Value("${app.task.daily}")
    private String dailyTaskList;


    @Value("${app.task.label.urgent}")
    private String urgentTaskLabel;
    @Value("${app.task.label.nurgent}")
    private String nurgentTaskLabel;

    @Value("${app.task.label.important}")
    private String importantTaskLabel;
    @Value("${app.task.label.nimportant}")
    private String nimportantTaskLabel;


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUser() {
        return user;
    }

    public String getInterestBoard() {
        return interestBoard;
    }

    public String getProgressBoard() {
        return progressBoard;
    }

    public String getCompleteTaskList() {
        return completeTaskList;
    }

    public String getInputTaskList() {
        return inputTaskList;
    }

    public String getDistributeTaskList() {
        return distributeTaskList;
    }

    public String getPauseTaskList() {
        return pauseTaskList;
    }

    public String getHabitTaskList() {
        return habitTaskList;
    }

    public String getDailyTaskList() {
        return dailyTaskList;
    }

    public String getUrgentTaskLabel() {
        return urgentTaskLabel;
    }

    public String getNurgentTaskLabel() {
        return nurgentTaskLabel;
    }

    public String getImportantTaskLabel() {
        return importantTaskLabel;
    }

    public String getNimportantTaskLabel() {
        return nimportantTaskLabel;
    }

    public HashMap<String,String> getIdNameMap(){
        return new HashMap<String, String>(){{
            put(completeTaskList,"complete");
            put(inputTaskList,"input");
            put(distributeTaskList,"distribute");
            put(pauseTaskList,"pause");
            put(habitTaskList,"habit");
            put(dailyTaskList,"daily");
        }};
    }
}
