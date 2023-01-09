package org.apache.dubbo.registry.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.registry.client.migration.MigrationInvoker;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ysw
 * @date 2023/1/3 14:34
 */

public class ServiceUtils {

    private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);
//
//    /**
//     * 校验dubbo api 是否可用
//     *
//     * @param obj
//     * @param apiName
//     * @return
//     */
//    public static boolean isAvailable(Object obj) {
//        try {
//            Node invoker = INVOKER_HASH_MAP.get(obj);
//            if (invoker == null) {
//                Field handler = obj.getClass().getDeclaredField("handler");
//                handler.setAccessible(true);
//                InvokerInvocationHandler iih = (InvokerInvocationHandler) handler.get(obj);
//                invoker = (Node) f_invoker.get(iih);
//                INVOKER_HASH_MAP.put(obj, invoker);
//            }
//            return invoker.isAvailable();
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
//    }

    public static boolean isAvailableWithMethod(Object obj, String methodName) {
        try {
            MigrationInvoker mInvoker = INVOKER_HASH_MAP.get(obj);
            if (mInvoker == null) {
                Field handler = obj.getClass().getDeclaredField("handler");
                handler.setAccessible(true);
                InvokerInvocationHandler iih = (InvokerInvocationHandler) handler.get(obj);
                mInvoker = (MigrationInvoker) f_invoker.get(iih);
            }
            Object registryDirectoryInvokerDelegateObj = mInvoker.getInvoker().getDirectory().getAllInvokers().get(0);
            if (getProviderUrlMethod == null) {
                getProviderUrlMethod = registryDirectoryInvokerDelegateObj.getClass().getMethod("getProviderUrl");
                getProviderUrlMethod.setAccessible(true);
            }
            URL providerURL = (URL) getProviderUrlMethod.invoke(registryDirectoryInvokerDelegateObj);
            // put into cache
            INVOKER_HASH_MAP.put(obj, mInvoker);
            String methods = providerURL.getParameter("methods");
            boolean contains = Arrays.asList(methods.split(",")).contains(methodName);
            return mInvoker.isAvailable() && contains;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    private static Field f_invoker;

    private static Method getProviderUrlMethod = null;

    static {
        try {
            f_invoker = InvokerInvocationHandler.class.getDeclaredField("invoker");
            f_invoker.setAccessible(true);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 缓存invoker
     */
    private static final ConcurrentHashMap<Object, MigrationInvoker> INVOKER_HASH_MAP = new ConcurrentHashMap<>();

//    private static final ConcurrentHashMap<Object, Object> PROVIDER_HASH_MAP = new ConcurrentHashMap<>();
}
