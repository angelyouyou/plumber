package com.hebaibai.plumber.component.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "conf")
public class Conf {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "key")
    private String key;

    @Column(name = "val")
    private String val;

}
