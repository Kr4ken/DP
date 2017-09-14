package com.kr4ken.dp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.julienvey.trello.domain.Card;

import javax.persistence.*;

@Entity
public class Interest {

    @Id
    @GeneratedValue
    private Long id;

    Interest() { // jpa only
    }

    public Interest(String name, InterestType type){
        this(   name,
                null,
                null,
                null,
                null,
                type,
                null,
                null,
                null
        );
    }

    public Interest(Interest other){
        this.name = other.name == null?this.name:other.name;
        this.img = other.img == null?this.img:other.img;
        this.source = other.source == null?this.source:other.source;
        this.season = other.season == null?this.season:other.season;
        this.stage = other.stage == null?this.stage:other.stage;
        this.type = other.type == null?this.type:other.type;
        this.ord = other.ord == null?this.ord:other.ord;
        this.comment = other.comment == null?this.comment:other.comment;
        this.trelloId = other.trelloId == null?this.name:other.trelloId;
    }

    public Interest(String name,
                    String img,
                    String source,
                    Integer season,
                    Integer stage,
                    InterestType type,
                    Integer ord,
                    String comment,
                    String trelloId) {
        this.name = name;
        this.img = img;
        this.source = source;
        this.season = season;
        this.stage = stage;
        this.type = type;
        this.ord = ord;
        this.comment = comment;
        this.trelloId = trelloId;
    }
    //Id соответствующей карточки в трелло
    public String trelloId;
    //Наименование интереса
    public String name;
    //URL изображения
    public String img;
    //URL источника
    public String source;
    //Этипа интереса (Сезон\Том)
    public Integer season;
    //Этипа интереса (Серия\Глава)
    public Integer stage;
    //Тип интереса
    @OneToOne
    public InterestType type;
    //Позиция интереса в списке
    public Integer ord;
    //Комментарий
    public String comment;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getSource() {
        return source;
    }

    public Integer getSeason() {
        return season;
    }

    public Integer getStage() {
        return stage;
    }

    public InterestType getType() {
        return type;
    }

    public Integer getOrd() {
        return ord;
    }

    public String getComment() {
        return comment;
    }

    public String getTrelloId() {
        return trelloId;
    }
}