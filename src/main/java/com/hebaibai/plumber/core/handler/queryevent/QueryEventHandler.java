package com.hebaibai.plumber.core.handler.queryevent;

import com.github.shyiko.mysql.binlog.event.QueryEventData;
import net.sf.jsqlparser.statement.Statement;

public interface QueryEventHandler {


    void handle(QueryEventData eventData, Statement statement);
}
