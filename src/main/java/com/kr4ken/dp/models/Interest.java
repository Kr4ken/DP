package com.kr4ken.dp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
                "",
                "",
                0,
                0,
                type,
                1,
                ""
        );
    }

    public Interest(String name,
                    String img,
                    String source,
                    Integer season,
                    Integer stage,
                    InterestType type,
                    Integer ord,
                    String comment) {
        this.name = name;
        this.img = img;
        this.source = source;
        this.season = season;
        this.stage = stage;
        this.type = type;
        this.ord = ord;
        this.comment = comment;
    }

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
}