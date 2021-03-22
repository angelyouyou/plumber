package com.hebaibai.plumber.core;

import lombok.Data;

import java.util.Map;

/**
 * 数据库事件中的数据
 *
 * @author hjx
 */
@Data
public class SqlEventData {

    public static final String TYPE_INSERT = "TYPE_INSERT";
    public static final String TYPE_UPDATE = "TYPE_UPDATE";
    public static final String TYPE_DELETE = "TYPE_DELETE";
    private String type;
    private String key[];
    private String targetDatabase;
    private String targetTable;
    private String sourceDatabase;
    private String sourceTable;
    /**
     * 变动前的数据
     * k: 字段名称
     * v: 字段值
     */
    private Map<String, String> befor;
    /**
     * 变动后的数据
     * k: 字段名称
     * v: 字段值
     */
    private Map<String, String> after;

    public SqlEventData(String type) {
        this.type = type;
    }


}
