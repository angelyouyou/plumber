package com.hebaibai.plumber.core.conversion;

/**
 * 从 long 转换为 String
 *
 * @author hjx
 */
public class LongToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean canConversion = from == long.class || from == Long.class;
        if (to == String.class && canConversion) {
            return true;
        }
        return false;
    }

    @Override
    public String conversion(Object obj) {
        return String.valueOf(obj);
    }
}
