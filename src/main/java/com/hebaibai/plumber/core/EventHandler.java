package com.hebaibai.plumber.core;

import com.github.shyiko.mysql.binlog.event.Event;
import com.hebaibai.plumber.config.TableSyncJob;

/**
 * binlog的事件处理器
 *
 * @author hjx
 */
public interface EventHandler {


    /**
     * 是否支持当前操作
     *
     * @param event
     * @return
     */
    boolean support(Event event);

    /**
     * 处理
     *
     * @param event
     * @param tableSyncJob
     * @return
     */
    void handle(Event event, TableSyncJob tableSyncJob);

}
