package uyun.whale.dubbo.rpc.cluster.router.gray;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.GRAY_VERSION;

/**
 * GrayDeployRouter
 */
public class GrayDeployRouter extends AbstractRouter {
    public static final String NAME = "GRAY_ROUTER";
    private static final int GRAY_ROUTER_DEFAULT_PRIORITY = 200;
    private static final Logger logger = LoggerFactory.getLogger(GrayDeployRouter.class);

    public GrayDeployRouter(URL url) {
        super(url);
        this.priority = GRAY_ROUTER_DEFAULT_PRIORITY;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            if (logger.isDebugEnabled()) {
                logger.debug("provider size is equal or less than 1, would not invoke gray route logic");
            }
            return invokers;
        }

        List<Invoker<T>> result = getGrayInvokers(url, invokers);
        if (CollectionUtils.isEmpty(result)) {
            if (logger.isDebugEnabled()) {
                logger.debug("provider size is 0 after route logic, return all providers");
            }
            return invokers;
        }
        return result;
    }

    /**
     * 消费方	提供方有无灰度节点	 匹配逻辑
     * 灰度	    有	             优先匹配一致版本匹配不到匹配最高版本
     * 灰度	    无	             全匹配 OK
     * 非灰	    全灰	             全匹配 OK
     * 非灰	    部分灰度	         匹配非灰节点 OK
     * 非灰	    无	             全匹配 OK
     * @param url refer url (consumer)
     * @param invokers provider list
     */
    private <T> List<Invoker<T>> getGrayInvokers(URL url, final List<Invoker<T>> invokers) {

        Map<String, String> consumer = url.toMap();
        String consumerVersion = consumer.get(GRAY_VERSION);
        if (consumerVersion == null) {
            consumerVersion = System.getProperty(GRAY_VERSION);
        }

        List<Invoker<T>> result = new ArrayList<>();

        String maxGrayVersion = null;
        for (Invoker<T> invoker : invokers) {
            // 寻找灰度版本一致节点
            Map<String, String> provider = invoker.getUrl().toMap();
            if (consumerVersion == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("consumer is not a gray node");
                }
                if (provider.get(GRAY_VERSION) == null) {
                    result.add(invoker);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("consumer is a gray node");
                }
                if (consumerVersion.equalsIgnoreCase(provider.get(GRAY_VERSION))) {
                    result.add(invoker);
                }
            }
            // 寻找灰度版本最高节点
            String pVersion = invoker.getUrl().toMap().get(GRAY_VERSION);
            if (pVersion != null && maxGrayVersion != null && pVersion.compareTo(maxGrayVersion) > 0) {
                maxGrayVersion = pVersion;
            }
            if (pVersion != null && maxGrayVersion == null) {
                maxGrayVersion = pVersion;
            }
        }

        // 颜色匹配和版本匹配有结果的情况, 匹配成功, 返回匹配结果
        if (result.size() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("find " + result.size() + " provider with gray version match logic");
            }
            return result;
        }

        // 消费端非灰度并且没有匹配到非灰节点, 此时提供端应该是全灰, 匹配所有节点
        if (consumerVersion == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("can not found gray version match provider, consumer is not a gray node so return all providers");
            }
            return invokers;
        } else {
            // 消费端灰度, 提供端全部是非灰, 匹配所有节点
            if (maxGrayVersion == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("can not found gray provider, consumer is a gray node so return all providers");
                }
                return invokers;
            } else { // 消费端是灰度时, 匹配提供者灰度版本最高节点
                for (Invoker<T> invoker : invokers) {
                    String pVersion = invoker.getUrl().toMap().get(GRAY_VERSION);
                    if (maxGrayVersion.equalsIgnoreCase(pVersion)) {
                        result.add(invoker);
                    }
                }
                if (logger.isDebugEnabled() && result.size() > 0) {
                    logger.debug("find " + result.size() + " provider with max gray version " + maxGrayVersion);
                }
            }
        }
        return result;
    }
}
