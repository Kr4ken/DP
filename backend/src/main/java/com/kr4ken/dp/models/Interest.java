package com.kr4ken.dp.models;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
public class Interest {

    @Id
    @GeneratedValue
    private Long id;
    //Id соответствующей карточки в трелло
    private String trelloId;
    //Наименование интереса
    private String name;
    //URL изображения
    // Ссылки очень длинные
    @Length(max = 2000)
    private String img;
    //URL источника
    private String source;
    //Этипа интереса (Сезон\Том)
    private Integer season;
    //Этипа интереса (Серия\Глава)
    private Integer stage;
    //Тип интереса
    @OneToOne
    private InterestType type;
    //Позиция интереса в списке
    private Double ord;
    //Комментарий
    private String description;

    Interest() { // jpa only
    }

    public Interest(Interest other) { // jpa only
        this.copy(other);
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

    public void copy(Interest other){
        this.name = other.name == null?this.name:other.name;
        this.img = other.img == null?this.img:other.img;
        this.source = other.source == null?this.source:other.source;
        this.season = other.season == null?this.season:other.season;
        this.stage = other.stage == null?this.stage:other.stage;
        this.type = other.type == null?this.type:other.type;
        this.ord = other.ord == null?this.ord:other.ord;
        this.description = other.description == null?this.description :other.description;
        this.trelloId = other.trelloId == null?this.trelloId:other.trelloId;
    }

    public Interest(String name,
                    String img,
                    String source,
                    Integer season,
                    Integer stage,
                    InterestType type,
                    Double ord,
                    String description,
                    String trelloId) {
        this.name = name;
        this.img = img;
        this.source = source;
        this.season = season;
        this.stage = stage;
        this.type = type;
        this.ord = ord;
        this.description = description;
        this.trelloId = trelloId;
    }

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

    public Double getOrd() {
        return ord;
    }

    public String getDescription() {
        return description;
    }

    public String getTrelloId() {
        return trelloId;
    }

    public void setOrd(Double ord) {
        this.ord = ord;
    }

    public void setTrelloId(String trelloId) {
        this.trelloId = trelloId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public void setType(InterestType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}