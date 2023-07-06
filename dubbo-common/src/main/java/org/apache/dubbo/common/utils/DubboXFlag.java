package org.apache.dubbo.common.utils;

/**
 * @author ysw
 * @date 2023/7/6 11:02
 */
public class DubboXFlag {
    /**
     * 当前线程处理的是否是 dubboX
     */
    public static  ThreadLocal<Boolean> DUBBOX_FLAG = new ThreadLocal<>();
}
