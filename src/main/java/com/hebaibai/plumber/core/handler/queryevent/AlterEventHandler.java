package com.hebaibai.plumber.core.handler.queryevent;

import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.hebaibai.plumber.core.utils.TableMateData;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 修改字段
 */
@Slf4j
@Component
public class AlterEventHandler implements QueryEventHandler {

    @Autowired
    @Qualifier("tableMateDataMap")
    private Map<String, TableMateData> tableMateDataMap;

    @Override
    public void handle(QueryEventData eventData, Statement statement) {
        String databaseName = eventData.getDatabase();
        String tableName = null;
        if (!(statement instanceof Alter)) {
            return;
        }
        Alter alter = (Alter) statement;
        Table table = alter.getTable();
        if (StringUtils.isBlank(databaseName)) {
            databaseName = table.getSchemaName();
            tableName = table.getName();
        }
        String key = databaseName + "." + tableName;
        log.info("{}", key);
        if (!tableMateDataMap.containsValue(key)) {
            return;
        }

    }
}
