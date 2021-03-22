package com.hebaibai.plumber.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;

/**
 * 获取数据库表的元数据工具类
 *
 * @author hjx
 */
@Slf4j
@Component
public class DataBaseComponent {

    /**
     * 获取数据库链接
     *
     * @param dataSource
     * @param sql
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> query(DataSource dataSource, String sql) throws Exception {
        List<Map<String, Object>> maps = new QueryRunner(dataSource).query(sql, new MapListHandler());
        return maps;
    }

    public Map<String, TableMateData> tableMateDataMap(DataSource dataSource) throws Exception {
        Map<String, TableMateData> tableMateDataMap = new HashMap<>();
        String sql = "select\n" +
                "\tTABLE_NAME,\n" +
                "\tTABLE_SCHEMA\n" +
                "from\n" +
                "\tinformation_schema.TABLES\n" +
                "where\n" +
                "\tTABLE_SCHEMA not in ('information_schema', 'performance_schema', 'mysql', 'sys')\n" +
                "group by\n" +
                "\tTABLE_NAME";
        List<Map<String, Object>> query = query(dataSource, sql);
        for (Map<String, Object> map : query) {
            String tableName = map.get("TABLE_NAME").toString();
            String tableSchema = map.get("TABLE_SCHEMA").toString();
            TableMateData tableMateData = tableMateData(dataSource, tableSchema, tableName);
            tableMateDataMap.put(tableSchema + "." + tableName, tableMateData);
        }
        return tableMateDataMap;
    }

    /**
     * 获取表结构描述信息
     *
     * @param table
     * @param database
     * @return
     */
    public TableMateData tableMateData(DataSource dataSource, String database, String table) throws Exception {
        //表结构sql
        //COLUMN_KEY:PRI主键约束
        //COLUMN_KEY:UNI唯一约束
        //COLUMN_KEY:MUL可以重复
        String sql = "select " +
                "COLUMN_NAME, " +
                "DATA_TYPE, " +
                "COLUMN_KEY, " +
                "COLUMN_COMMENT " +
                "from " +
                "information_schema.COLUMNS " +
                "where " +
                "TABLE_SCHEMA = '" + database + "' and " +
                "TABLE_NAME = '" + table + "' " +
                "order by " +
                "ORDINAL_POSITION asc";
        List<Map<String, Object>> maps = query(dataSource, sql);
        TableMateData mateData = new TableMateData();
        mateData.setIds(getKey(maps));
        mateData.setNama(table);
        mateData.setDataBase(database);
        mateData.setColumns(getColumnName(maps));
        mateData.setColumnType(getColumnType(maps));
        mateData.setColumnDoc(getColumnDoc(maps));
        return mateData;
    }

    private Map<String, String> getColumnDoc(List<Map<String, Object>> maps) {
        Map<String, String> columnDocs = new HashMap<>();
        for (Map<String, Object> map : maps) {
            Object columnComment = map.get("COLUMN_COMMENT");
            Object columnName = map.get("COLUMN_NAME");
            columnDocs.put(columnName.toString(), columnComment.toString());
        }
        return columnDocs;
    }

    private Map<String, String> getColumnType(List<Map<String, Object>> maps) {
        Map<String, String> columnTypes = new HashMap<>();
        for (Map<String, Object> map : maps) {
            Object dataType = map.get("DATA_TYPE");
            Object columnName = map.get("COLUMN_NAME");
            columnTypes.put(columnName.toString(), dataType.toString());
        }
        return columnTypes;
    }

    private List<String> getColumnName(List<Map<String, Object>> maps) {
        List<String> columnNames = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Object columnName = map.get("COLUMN_NAME");
            columnNames.add(columnName.toString());
        }
        return columnNames;
    }


    /**
     * 获取主键列表
     *
     * @param maps
     * @return
     */
    private String[] getKey(List<Map<String, Object>> maps) {
        List<String> keys = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Object columnKey = map.get("COLUMN_KEY");
            if (Objects.equals("PRI", columnKey)) {
                Object columnName = map.get("COLUMN_NAME");
                keys.add(columnName.toString());
            }
        }
        return keys.toArray(new String[]{});
    }


}

