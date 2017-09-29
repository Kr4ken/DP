package com.kr4ken.dp.models;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    //Id соответствующей карточки в трелло
    public String trelloId;
    //Наименование Таска
    public String name;
    //URL изображения
    // Ссылки очень длинные
    @Length(max = 2000)
    public String img;
}
