package com.kr4ken.dp.models.hooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kr4ken.habitica.domain.Task;

/**
 * Хук приходящий из хабитики
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HabiticaHook {
    private String type;
    private String direction;
    private Double delta;
    private Task task;

    public HabiticaHook() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
