package com.hebaibai.plumber.core.utils;

import com.github.shyiko.mysql.binlog.event.*;
import com.hebaibai.plumber.core.conversion.Conversion;
import com.hebaibai.plumber.core.conversion.ConversionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hjx
 */
@Slf4j
@Component
public class EventDataComponent {

    @Autowired
    private ConversionFactory conversionFactory;

    public Long getTableId(EventData eventData) {
        DeleteRowsEventData deleteRowsEventData = getDeleteRowsEventData(eventData);
        if (deleteRowsEventData != null) {
            return deleteRowsEventData.getTableId();
        }
        UpdateRowsEventData updateRowsEventData = getUpdateRowsEventData(eventData);
        if (updateRowsEventData != null) {
            return updateRowsEventData.getTableId();
        }
        WriteRowsEventData writeRowsEventData = getWriteRowsEventData(eventData);
        if (writeRowsEventData != null) {
            return writeRowsEventData.getTableId();
        }
        TableMapEventData tableMapEventData = getTableMapEventData(eventData);
        if (tableMapEventData != null) {
            return tableMapEventData.getTableId();
        }
        return null;
    }

    public WriteRowsEventData getWriteRowsEventData(EventData eventData) {
        if (eventData instanceof WriteRowsEventData) {
            return (WriteRowsEventData) eventData;
        }
        return null;
    }

    public DeleteRowsEventData getDeleteRowsEventData(EventData eventData) {
        if (eventData instanceof DeleteRowsEventData) {
            return (DeleteRowsEventData) eventData;
        }
        return null;
    }

    public UpdateRowsEventData getUpdateRowsEventData(EventData eventData) {
        if (eventData instanceof UpdateRowsEventData) {
            return (UpdateRowsEventData) eventData;
        }
        return null;
    }

    public TableMapEventData getTableMapEventData(EventData eventData) {
        if (eventData instanceof TableMapEventData) {
            return (TableMapEventData) eventData;
        }
        return null;
    }

    public String[] getBeforUpdate(EventData eventData) {
        UpdateRowsEventData updateRowsEventData = getUpdateRowsEventData(eventData);
        if (updateRowsEventData == null) {
            return null;
        }
        List<Map.Entry<Serializable[], Serializable[]>> rows = updateRowsEventData.getRows();
        if (rows.size() == 0) {
            return null;
        }
        Map.Entry<Serializable[], Serializable[]> entry = rows.get(0);
        //key befor
        return values(entry.getKey());
    }

    public String[] getAfterUpdate(EventData eventData) {
        UpdateRowsEventData updateRowsEventData = getUpdateRowsEventData(eventData);
        if (updateRowsEventData == null) {
            return null;
        }
        List<Map.Entry<Serializable[], Serializable[]>> rows = updateRowsEventData.getRows();
        if (rows.size() == 0) {
            return null;
        }
        Map.Entry<Serializable[], Serializable[]> entry = rows.get(0);
        //value after
        return values(entry.getValue());
    }

    private String[] values(Serializable[] values) {
        String[] val = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Serializable v = values[i];
            if (v == null) {
                val[i] = null;
                continue;
            }
            Conversion<String> conversion = conversionFactory.getConversion(String.class, v.getClass());
            val[i] = conversion.conversion(v);
        }
        return val;
    }

    /**
     * 修改 bug : 执行 insert into table values(111),(222);时 222 无法同步 2020年04月16日10:29:31
     *
     * @param data
     * @return
     */
    public String[][] getInsertRows(EventData data) {
        WriteRowsEventData writeRowsEventData = getWriteRowsEventData(data);
        if (writeRowsEventData == null) {
            return null;
        }
        List<Serializable[]> rows = writeRowsEventData.getRows();
        if (rows.size() == 0) {
            return null;
        }
        String[][] values = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            values[i] = values(rows.get(i));
        }
        return values;
    }

    public String[] getDeleteRows(EventData data) {
        DeleteRowsEventData deleteRowsEventData = getDeleteRowsEventData(data);
        if (deleteRowsEventData == null) {
            return null;
        }
        List<Serializable[]> rows = deleteRowsEventData.getRows();
        if (rows.size() == 0) {
            return null;
        }
        return values(rows.get(0));
    }
}
