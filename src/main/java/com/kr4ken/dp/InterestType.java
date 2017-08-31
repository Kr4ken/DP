package com.kr4ken.dp;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class InterestType {
    @JsonIgnore
    @OneToOne
    private Interest interest;

    @Id
    @GeneratedValue
    private Long id;

    InterestType() { // jpa only
    }

    public InterestType(String name,String description)
    {
        this.name = name;
        this.description = description;
    }

    public String name;
    public String description;

    public String getDescription() {
        return description;
    }

    public Interest getInterest() {
        return interest;
    }

    public String getName() {
        return name;
    }
}
