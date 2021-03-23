package com.hebaibai.plumber.core.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据转换工厂类
 *
 * @author hjx
 */
@Component
public class ConversionFactory {

    @Autowired
    private List<Conversion> conversions;

    public <T> Conversion<T> getConversion(Class<T> to, Class from) {
        for (Conversion conversion : conversions) {
            if (conversion.support(to, from)) {
                return conversion;
            }
        }
        throw new UnsupportedOperationException("不支持的转换：" + from);
    }

}
