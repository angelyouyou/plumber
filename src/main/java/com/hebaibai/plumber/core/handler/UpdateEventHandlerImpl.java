package com.hebaibai.plumber.core.handler;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventHeader;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.hebaibai.plumber.Style;
import com.hebaibai.plumber.component.EntityService;
import com.hebaibai.plumber.config.TableSyncJob;
import com.hebaibai.plumber.core.EventHandler;
import com.hebaibai.plumber.core.SqlEventData;
import com.hebaibai.plumber.core.utils.EventDataUtils;
import com.hebaibai.plumber.core.utils.TableMateData;
import io.vertx.mysqlclient.MySQLPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 更新事件处理器
 *
 * @author hjx
 */
@Slf4j
@Component
public class UpdateEventHandlerImpl implements EventHandler {

    @Autowired
    @Qualifier("tableMateDataMap")
    protected Map<String, TableMateData> tableMateDataMap;

    @Autowired
    protected EntityService entityService;

    @Autowired
    protected MySQLPool mySQLPool;

    @Override
    public boolean support(Event event) {
        EventHeader header = event.getHeader();
        if (EventType.isUpdate(header.getEventType())) {
            return true;
        }
        return false;
    }

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
        String[] befor = EventDataUtils.getBeforUpdate(data);
        String[] after = EventDataUtils.getAfterUpdate(data);
        //拼装sql需要的数据
        List<String> columns = tableMateData.getColumns();
        Map<String, String> eventBeforData = new HashMap<>();
        Map<String, String> eventAfterData = new HashMap<>();
        Map<String, String> mapping = tableSyncJob.getMapping();
        for (int i = 0; i < columns.size(); i++) {
            String sourceName = columns.get(i);
            String targetName = mapping.get(sourceName);
            if (targetName == null) {
                continue;
            }
            //设置更新前的数据
            eventBeforData.put(targetName, befor[i]);
            //设置更新后的数据
            eventAfterData.put(targetName, after[i]);
        }
        //填充插件数据
        SqlEventData sqlEventData = new SqlEventData(SqlEventData.TYPE_UPDATE);
        //添加变动前的数据
        sqlEventData.setBefor(eventBeforData);
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
        //拼装sql
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> wheres = new ArrayList<>();
        List<String> updates = new ArrayList<>();
        for (Map.Entry<String, String> entry : sqlEventData.getAfter().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //变动之前的值
            String beforValue = sqlEventData.getBefor().get(key);
            //数据没有变化的，跳过
            if (Objects.equals(value, beforValue)) {
                continue;
            }
            if (value == null) {
                updates.add("`" + key + "` = null");
            } else {
                updates.add("`" + key + "` = '" + value + "'");
            }
        }
        for (String key : sqlEventData.getKey()) {
            String value = sqlEventData.getBefor().get(key);
            if (value == null) {
                wheres.add(key + " = null ");
            } else {
                wheres.add("`" + key + "`" + " = '" + value + "' ");
            }
        }
        // 没有更新项, 跳过
        if (updates.size() == 0) {
            return;
        }
        sqlBuilder.append("UPDATE ")
                .append(sqlEventData.getTargetDatabase())
                .append(".")
                .append(sqlEventData.getTargetTable())
                .append(" SET ")
                .append(String.join(", ", updates))
                .append(" WHERE ")
                .append(String.join("AND ", wheres));
        String sql = sqlBuilder.toString();
        log.info(Style.style(Style.FontColor.GREEN).str(sql));
        //执行sql
        mySQLPool.preparedQuery(sql)
                .execute()
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    log.error(Style.error().str("sql-update:"), throwable);
                });
    }

    protected String[] ids(TableSyncJob tableSyncJob) {
        String primaryKey = tableSyncJob.getPrimaryKey();
        Map<String, String> mapping = tableSyncJob.getMapping();
        return new String[]{mapping.getOrDefault(primaryKey, primaryKey)};
    }
}
