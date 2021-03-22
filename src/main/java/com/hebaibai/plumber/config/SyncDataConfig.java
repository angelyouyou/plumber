package com.hebaibai.plumber.config;

import lombok.Data;

import java.util.List;

/**
 * 程序配置
 *
 * @author hjx
 */
@Data
public class SyncDataConfig {

    /**
     * binlog name
     */
    private String logName;
    /**
     * binlog 位置
     */
    private Long position;

    private DataSourceConfig dataSource;

    private DataSourceConfig dataTarget;

    private List<String> executer;

    private List<TableSyncJob> tableSyncJob;

}
