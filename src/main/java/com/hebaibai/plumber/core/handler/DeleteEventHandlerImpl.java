package com.hebaibai.plumber.core.handler;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventHeader;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.hebaibai.plumber.Style;
import com.hebaibai.plumber.config.TableSyncJob;
import com.hebaibai.plumber.core.EventHandler;
import com.hebaibai.plumber.core.SqlEventData;
import com.hebaibai.plumber.core.utils.TableMateData;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 删除事件处理器
 *
 * @author hjx
 */
@Slf4j
@Component
public class DeleteEventHandlerImpl extends UpdateEventHandlerImpl implements EventHandler {

    @Override
    public boolean support(Event event) {
        EventHeader header = event.getHeader();
        if (EventType.isDelete(header.getEventType())) {
            return true;
        }
        return false;
    }

    @Override
    public void handle(Event event, TableSyncJob tableSyncJob) {
        EventData data = event.getData();
        Long tableId = eventDataComponent.getTableId(data);
        String tableName = entityService.getTableName(tableId);
        String databaseName = entityService.getDatabaseName(tableId);
        if (tableId == null || tableName == null || databaseName == null) {
            return;
        }
        String key = databaseName + "." + tableName;
        if (!tableMateDataMap.containsKey(key)) {
            return;
        }
        if (!key.equals(tableSyncJob.getSource())) {
            return;
        }
        log.info("{}.{}", databaseName, tableName);
        TableMateData tableMateData = tableMateDataMap.get(key);
        Map<String, String> mapping = tableSyncJob.getMapping();
        String[] rows = eventDataComponent.getDeleteRows(data);
        List<String> columns = tableMateData.getColumns();
        Map<String, String> eventAfterData = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            String sourceName = columns.get(i);
            String value = rows[i];
            String targetName = mapping.get(sourceName);
            eventAfterData.put(targetName, value);
        }
        //填充插件数据
        SqlEventData sqlEventData = new SqlEventData(SqlEventData.TYPE_DELETE);
        //添加变动后的数据
        sqlEventData.setAfter(eventAfterData);
        sqlEventData.setSourceDatabase(databaseName);
        sqlEventData.setSourceTable(tableName);
        String target = tableSyncJob.getTarget();
        String[] split = target.split("\\.");
        sqlEventData.setTargetDatabase(split[0]);
        sqlEventData.setTargetTable(split[1]);
        sqlEventData.setKey(ids(tableSyncJob));
        execute(sqlEventData);
    }


    /**
     * 拼装sql, 并执行
     *
     * @param sqlEventData
     */
    private void execute(SqlEventData sqlEventData) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> wheres = new ArrayList<>();
        for (String key : sqlEventData.getKey()) {
            String value = sqlEventData.getAfter().get(key);
            if (value == null) {
                wheres.add(key + " = null ");
            } else {
                wheres.add("`" + key + "`" + " = '" + value + "' ");
            }
        }
        sqlBuilder.append("DELETE FROM ")
                .append(sqlEventData.getTargetDatabase())
                .append(".")
                .append(sqlEventData.getTargetTable())
                .append(" WHERE ")
                .append(String.join("and ", wheres));
        String sql = sqlBuilder.toString();
        //执行sql
        String sqlBeautify = FormatStyle.BASIC.getFormatter().format(sql);
        log.info(Style.style(Style.FontColor.GREEN).str(sqlBeautify));
        mySQLPool.preparedQuery(sql)
                .execute()
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    log.error(Style.error().str("sql-delete:"), throwable);
                });
    }
}
