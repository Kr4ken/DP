package com.kr4ken.habitica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kr4ken.habitica.Habitica;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HabiticaEntity {

    @JsonIgnore
    protected Habitica habiticaService;

    public void setHabiticaService(Habitica habiticaService) {
        this.habiticaService = habiticaService;
    }
}
