package org.apache.dubbo.rpc.support;

import org.apache.dubbo.common.Node;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ysw
 * @date 2023/1/3 14:34
 */

public class ServiceUtils {

    private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

    /**
     * 校验dubbo api 是否可用
     *
     * @param obj
     * @param apiName
     * @return
     */
    public static boolean isAvailable(Object obj) {
        try {
            Node invoker = INVOKER_HASH_MAP.get(obj);
            if (invoker == null) {
                Field handler = obj.getClass().getDeclaredField("handler");
                handler.setAccessible(true);
                InvokerInvocationHandler iih = (InvokerInvocationHandler) handler.get(obj);
                invoker = (Node) f_invoker.get(iih);
                INVOKER_HASH_MAP.put(obj, invoker);
            }
            return invoker.isAvailable();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private static Field f_invoker = null;
    static {
        try {
            f_invoker=  InvokerInvocationHandler.class.getDeclaredField("invoker");
            f_invoker.setAccessible(true);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 缓存invoker
     */
    private static final ConcurrentHashMap<Object, Node> INVOKER_HASH_MAP = new ConcurrentHashMap<> ();
}
