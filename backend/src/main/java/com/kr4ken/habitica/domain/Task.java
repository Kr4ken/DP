package com.kr4ken.habitica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task  extends HabiticaEntity{
    private String id;
    private String userId;
    private String alias;
    private String text;
    private Boolean isDue;
    private Date createdAt;
    private Date updatedAt;
    private String atribute;
    private Double value;
    private Double priority;
    private String notes;
    // Может поменять
    private String type;
    private Boolean completed;
    private Boolean yesterDaily;

}
