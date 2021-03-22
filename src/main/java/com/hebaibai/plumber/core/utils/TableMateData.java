package com.hebaibai.plumber.core.utils;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据库表元信息
 *
 * @author hjx
 */
@Data
public class TableMateData {

    /**
     * 表名称
     */
    private String nama;

    /**
     * id 名称
     */
    private String[] ids;

    /**
     * 库名称
     */
    private String dataBase;

    private List<String> columns;

    private Map<String, String> columnType;

    private Map<String, String> columnDoc;

}
