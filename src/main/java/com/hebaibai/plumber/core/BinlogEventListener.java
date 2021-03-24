package com.hebaibai.plumber.core;

import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;
import com.hebaibai.plumber.PlumberMain;
import com.hebaibai.plumber.config.TableSyncJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 监听数据库的binlog事件
 *
 * @author hjx
 */
@Slf4j
@Component
public class BinlogEventListener implements EventListener {

    @Autowired
    private List<EventHandler> eventHandlers;

    /**
     * 实现监听事件
     *
     * @param event
     */
    @Override
    public void onEvent(Event event) {
        //循环处理,可能一次事件由多个handle共同处理
        for (EventHandler handle : eventHandlers) {
            boolean support = handle.support(event);
            if (!support) {
                continue;
            }
            try {
                handle.handle(event);
            } catch (Exception e) {
                log.error("error", e);
            }
            for (TableSyncJob tableSyncJob : PlumberMain.CONFIG.getTableSyncJob()) {
                handle.handle(event, tableSyncJob);
            }
        }
    }

}
