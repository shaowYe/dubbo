package org.apache.dubbo.registry.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.registry.client.migration.MigrationInvoker;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
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
     * @return
     */
    public static boolean isAvailable(Object obj) {
        try {
            MigrationInvoker mInvoker = INVOKER_HASH_MAP.get(obj);
            if (mInvoker == null) {
                Field handler = obj.getClass().getDeclaredField("handler");
                handler.setAccessible(true);
                InvokerInvocationHandler iih = (InvokerInvocationHandler) handler.get(obj);
                mInvoker = (MigrationInvoker) f_invoker.get(iih);
                // put into cache
                INVOKER_HASH_MAP.put(obj, mInvoker);
            }
            return mInvoker.isAvailable();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 校验dubbo api 和 method 是否可用
     * @param obj
     * @param methodName
     * @return
     */
    public static boolean isAvailableWithMethod(Object obj, String methodName) {
        try {
            MigrationInvoker mInvoker = INVOKER_HASH_MAP.get(obj);
            if (mInvoker == null) {
                Field handler = obj.getClass().getDeclaredField("handler");
                handler.setAccessible(true);
                InvokerInvocationHandler iih = (InvokerInvocationHandler) handler.get(obj);
                mInvoker = (MigrationInvoker) f_invoker.get(iih);
                // put into cache
                INVOKER_HASH_MAP.put(obj, mInvoker);
            }
            if (!mInvoker.isAvailable()) {
                return false;
            }
            List<Invoker> allInvokers = mInvoker.getInvoker().getDirectory().getAllInvokers();
            if (CollectionUtils.isEmpty(allInvokers)) {
                return false;
            }
            if (getProviderUrlMethod == null) {
                Object registryDirectoryInvokerDelegateObj = allInvokers.get(0);
                getProviderUrlMethod = registryDirectoryInvokerDelegateObj.getClass().getMethod("getProviderUrl");
                getProviderUrlMethod.setAccessible(true);
            }

            String methodCheck = methodName + ",";
            return allInvokers.stream().map(ServiceUtils::getURL).anyMatch(url -> {
                String methods = url.getParameter("methods");
                return (methods + ",").contains(methodCheck);
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private static URL getURL(Object invoker) {
        try {
            return (URL) getProviderUrlMethod.invoke(invoker);
        } catch (Exception e) {
            throw new IllegalStateException(e);
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

}
