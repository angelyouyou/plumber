package com.hebaibai.plumber.core.conversion;

/**
 * 从 Double 转换为 String
 *
 * @author hjx
 */
public class DoubleToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean ok = to == Double.class || to == double.class;
        if (ok && from == String.class) {
            return true;
        }
        return false;
    }

    @Override
    public String conversion(Object obj) {
        return obj.toString();
    }
}
