package com.hebaibai.plumber.core.handler;

import com.github.shyiko.mysql.binlog.event.*;
import com.hebaibai.plumber.component.EntityService;
import com.hebaibai.plumber.core.EventHandler;
import com.hebaibai.plumber.core.handler.queryevent.QueryEventHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ROTATE 事件处理器
 *
 * @author hjx
 */
@Slf4j
@Component
public class QueryEventHandlerImpl implements EventHandler {

    @Autowired
    protected EntityService entityService;

    @Autowired
    private List<QueryEventHandler> eventHandlers;

    @Override
    public boolean support(Event event) {
        EventHeader header = event.getHeader();
        if (EventType.QUERY == header.getEventType()) {
            return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void handle(Event event) {
        EventData data = event.getData();
        if (!(data instanceof QueryEventData)) {
            return;
        }
        QueryEventData eventData = (QueryEventData) data;
        String sql = eventData.getSql();
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            for (QueryEventHandler eventHandler : eventHandlers) {
                eventHandler.handle(eventData, statement);
            }
        } catch (JSQLParserException e) {
            log.debug("CCJSqlParserUtil.parse(sql) error, sql: {}", sql);
        }
    }

}
