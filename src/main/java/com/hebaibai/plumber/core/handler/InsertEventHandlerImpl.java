package com.hebaibai.plumber.core.handler;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventHeader;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.hebaibai.plumber.Style;
import com.hebaibai.plumber.config.TableSyncJob;
import com.hebaibai.plumber.core.EventHandler;
import com.hebaibai.plumber.core.SqlEventData;
import com.hebaibai.plumber.core.utils.EventDataUtils;
import com.hebaibai.plumber.core.utils.TableMateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插入事件处理器
 *
 * @author hjx
 */
@Slf4j
@Component
public class InsertEventHandlerImpl extends UpdateEventHandlerImpl implements EventHandler {

    @Override
    public boolean support(Event event) {
        EventHeader header = event.getHeader();
        if (EventType.isWrite(header.getEventType())) {
            return true;
        }
        return false;
    }

    /**
     * 修改 bug : 执行 insert into table values(111),(222);时 222 无法同步 2020年04月16日10:29:31
     *
     * @param event
     * @param tableSyncJob
     */
    @Override
    public void handle(Event event, TableSyncJob tableSyncJob) {
        EventData data = event.getData();
        Long tableId = EventDataUtils.getTableId(data);
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
        String[][] insertRowsArray = EventDataUtils.getInsertRows(data);
        for (int x = 0; x < insertRowsArray.length; x++) {
            String[] rows = insertRowsArray[x];
            List<String> columns = tableMateData.getColumns();
            Map<String, String> mapping = tableSyncJob.getMapping();
            Map<String, String> eventAfterData = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                String sourceName = columns.get(i);
                String value = rows[i];
                if (!mapping.containsKey(sourceName)) {
                    continue;
                }
                //目标字段
                String targetName = mapping.get(sourceName);
                if (targetName == null) {
                    continue;
                }
                eventAfterData.put(targetName, value);
            }
            //填充插件数据
            SqlEventData sqlEventData = new SqlEventData(SqlEventData.TYPE_INSERT);
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
    }

    /**
     * 拼装sql, 并执行
     *
     * @param sqlEventData
     */
    private void execute(SqlEventData sqlEventData) {
        //拼装sql
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> columns = new ArrayList<>();
        List<String> columnValues = new ArrayList<>();
        for (Map.Entry<String, String> entry : sqlEventData.getAfter().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            columns.add("`" + key + "`");
            if (value == null) {
                columnValues.add("null");
            } else {
                columnValues.add("'" + value + "'");
            }
        }
        sqlBuilder.append("REPLACE INTO ")
                .append(sqlEventData.getTargetDatabase())
                .append(".")
                .append(sqlEventData.getTargetTable())
                .append(" ( ").append(String.join(", ", columns))
                .append(" ) VALUES ( ").append(String.join(", ", columnValues))
                .append(");");
        String sql = sqlBuilder.toString();
        log.info(Style.style(Style.FontColor.GREEN).str(sql));
        mySQLPool.preparedQuery(sql)
                .execute()
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    log.error(Style.error().str("sql-insert:"), throwable);
                });
    }
}
