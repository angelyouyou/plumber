package com.hebaibai.plumber.config;

import lombok.Data;

import java.util.Map;

@Data
public class TableSyncJob {

    /**
     * 字段对应关系
     * k: 来源字段
     * v: 目标字段
     */
    private Map<String, String> mapping;

    private String primaryKey;

    private String source;

    private String target;

}