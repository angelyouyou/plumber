package com.hebaibai.plumber.core.conversion;

import org.springframework.stereotype.Component;

/**
 * 从 String 转换为 String
 *
 * @author hjx
 */
@Component
public class StringlToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean canConversion = from == String.class;
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
