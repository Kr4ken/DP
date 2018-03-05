package com.kr4ken.dp.models.hooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kr4ken.habitica.domain.Task;

import java.util.Date;

/**
 * Хук получаемый из трелло
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrelloHook {
    private TrelloActionHook action;

    public TrelloHook() {
    }

    public TrelloActionHook getAction() {
        return action;
    }

    public void setAction(TrelloActionHook action) {
        this.action = action;
    }
}
