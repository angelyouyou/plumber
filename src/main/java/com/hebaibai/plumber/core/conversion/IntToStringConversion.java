package com.hebaibai.plumber.core.conversion;

import org.springframework.stereotype.Component;

/**
 * 从 int 转换为 String
 *
 * @author hjx
 */
@Component
public class IntToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        boolean canConversion = from == int.class || from == Integer.class;
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
