package com.hebaibai.plumber.component.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "database_id")
public class DataBaseId {

    /**
     * 主键 : id,  id
     */
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "database_name")
    private String databaseName;

}
