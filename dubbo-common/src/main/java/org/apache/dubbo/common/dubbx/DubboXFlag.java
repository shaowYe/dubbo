package org.apache.dubbo.common.dubbx;

import org.apache.dubbo.common.threadlocal.InternalThreadLocal;

/**
 * @author ysw
 * @date 2023/11/9 17:02
 */
public class DubboXFlag {
    /**
     * 当前线程处理的是否是 dubboX
     */
    public static final ThreadLocal<Boolean> DUBBOX_FLAG = new ThreadLocal<>();
}
