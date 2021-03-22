package com.hebaibai.plumber.core.handler;

import com.github.shyiko.mysql.binlog.event.*;
import com.hebaibai.plumber.component.EntityService;
import com.hebaibai.plumber.config.TableSyncJob;
import com.hebaibai.plumber.core.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TABLE_MAP 事件处理器
 *
 * @author hjx
 */
@Slf4j
@Component
public class TableMapEventHandlerImpl implements EventHandler {

    @Autowired
    protected EntityService entityService;

    @Override
    public boolean support(Event event) {
        EventHeader header = event.getHeader();
        if (EventType.TABLE_MAP == header.getEventType()) {
            return true;
        }
        return false;
    }

    @Override
    public void handle(Event event, TableSyncJob tableSyncJob) {
        EventData data = event.getData();
        if (data instanceof TableMapEventData) {
            TableMapEventData eventData = (TableMapEventData) data;
            long tableId = eventData.getTableId();
            String tableName = eventData.getTable();
            String databaseName = eventData.getDatabase();
            entityService.seveTableName(tableId, tableName);
            entityService.seveDatabaseName(tableId, databaseName);
            return;
        }
    }

}
