package com.hebaibai.plumber.core.conversion;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

/**
 * 数据转换工厂类
 *
 * @author hjx
 */
@UtilityClass
public class ConversionFactory {

    private static final List<Conversion> CONVERSION_LIST = Arrays.asList(
            new StringlToStringConversion(),
            new TimestampToStringConversion(),
            new BigDecimalToStringConversion(),
            new BytesToStringConversion(),
            new IntToStringConversion(),
            new DateToStringConversion(),
            new SqlDateToStringConversion(),
            new DoubleToStringConversion(),
            new LongToStringConversion()
    );

    public static <T> Conversion<T> getConversion(Class<T> to, Class from) {

        for (Conversion conversion : CONVERSION_LIST) {
            if (conversion.support(to, from)) {
                return conversion;
            }
        }
        throw new UnsupportedOperationException("不支持的转换：" + from);
    }

}
