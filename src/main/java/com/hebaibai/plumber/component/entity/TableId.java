package com.hebaibai.plumber.component.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "table_id")
public class TableId {

    /**
     * 主键 : id,  id
     */
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "table_name")
    private String tableName;

}
