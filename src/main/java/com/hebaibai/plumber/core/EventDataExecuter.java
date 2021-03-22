package com.hebaibai.plumber.core;

/**
 * 事件插件, 用于处理其他以一些东西
 *
 * @author hjx
 */
public interface EventDataExecuter {

    /**
     * 开始处理数据
     *
     * @return
     */
    void execute(SqlEventData sqlEventData);

}
