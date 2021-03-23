package com.hebaibai.plumber.core.conversion;

import org.springframework.stereotype.Component;

/**
 * 从 byte[] 转换为 String
 *
 * @author hjx
 */
@Component
public class BytesToStringConversion implements Conversion<String> {

    @Override
    public boolean support(Class to, Class from) {
        if (to == String.class && from == byte[].class) {
            return true;
        }
        return false;
    }

    @Override
    public String conversion(Object obj) {
        return new String((byte[]) obj);
    }
}
