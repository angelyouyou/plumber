package com.hebaibai.plumber.core.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 从 Date 转换为 String
 *
 * @author hjx
 */
public class DateToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean canConversion = from == Date.class;
        if (to == String.class && canConversion) {
            return true;
        }
        return false;
    }

    @Override
    public String conversion(Object obj) {
        Date date = (Date) obj;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        return dateStr;
    }
}
