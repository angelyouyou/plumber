package com.hebaibai.plumber.core.conversion;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 从 Timestamp 转换为 String
 *
 * @author hjx
 */
public class TimestampToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean canConversion = from == Timestamp.class;
        if (to == String.class && canConversion) {
            return true;
        }
        return false;
    }

    @Override
    public String conversion(Object obj) {
        Timestamp timestamp = (Timestamp) obj;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(timestamp);
        return date;
    }
}
