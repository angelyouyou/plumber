package com.hebaibai.plumber.core.conversion;

import java.math.BigDecimal;

/**
 * 从 BigDecimal 转换为 String
 *
 * @author hjx
 */
public class BigDecimalToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean canConversion = from == BigDecimal.class;
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
