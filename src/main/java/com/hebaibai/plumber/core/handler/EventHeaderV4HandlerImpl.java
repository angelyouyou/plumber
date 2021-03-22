package com.hebaibai.plumber.core.handler;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeader;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.hebaibai.plumber.component.EntityService;
import com.hebaibai.plumber.config.TableSyncJob;
import com.hebaibai.plumber.core.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * EventHeaderV4 事件处理器
 *
 * @author hjx
 */
@Slf4j
@Component
public class EventHeaderV4HandlerImpl implements EventHandler {

    @Autowired
    protected EntityService entityService;

    @Override
    public boolean support(Event event) {
        EventHeader header = event.getHeader();
        if (header instanceof EventHeaderV4) {
            return true;
        }
        return false;
    }

    @Override
    public void handle(Event event, TableSyncJob tableSyncJob) {
        EventHeader header = event.getHeader();
        long nowPosition = ((EventHeaderV4) header).getPosition();
        if (nowPosition < 0) {
            return;
        }
        entityService.savePosition(nowPosition);
    }

}
