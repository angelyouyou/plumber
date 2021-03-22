package com.hebaibai.plumber.core.conversion;

/**
 * 数据转换
 *
 * @author hjx
 */
public interface Conversion<To> {

    /**
     * 是否支持转换
     *
     * @param from 从什么
     * @param to   转换成什么
     * @return
     */
    boolean support(Class to, Class from);

    /**
     * 将数据进行转换
     *
     * @param from
     * @return
     */
    To conversion(Object from);
}
