package uyun.whale.dubbo.registry;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.UrlUtils;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.zookeeper.ZookeeperRegistry;
import org.apache.dubbo.remoting.zookeeper.ChildListener;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.dubbo.common.constants.CommonConstants.*;
import static org.apache.dubbo.common.constants.RegistryConstants.*;

/**
 * 支持层级代理机制的 Zookeeper 注册中心实现。
 *
 * @author hesy
 */
public class DelegateZookeeperRegistry extends ZookeeperRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DelegateZookeeperRegistry.class);
    private ZookeeperClient parentZkClient;
    private Map<URL, Boolean> disabledServices = new HashMap<>();
    private Map<URL, Boolean> useParentProviders = new HashMap<>();


    public DelegateZookeeperRegistry(URL url, URL parentZkUrl, ZookeeperTransporter zookeeperTransporter) {
        super(url, zookeeperTransporter);

        if (parentZkUrl != null) {
            logger.debug("Parent Registry: {}", parentZkUrl);
            try {
                parentZkClient = zookeeperTransporter.connect(parentZkUrl);
            } catch (Exception e) {
                logger.error("Failed to connect delegate zookeeper: {}", parentZkUrl, e);
            }
        }
    }

    /**
     * Notify changes from the Provider side.
     *
     * @param url      consumer side url
     * @param listener listener
     * @param urls     provider latest urls
     */
    @Override
    protected void notify(URL url, NotifyListener listener, List<URL> urls) {
        // 未配置上级注册中心，获取当前 URL 是服务提供者
        if (parentZkClient == null || PROVIDER.equals(url.getProtocol()) || url.getServiceInterface().contains("ResObjectService")) {
            super.notify(url, listener, urls);
            return;
        }

        // 如果之前使用的是上级注册中心提供的服务
        if (useParentProviders.remove(url) == Boolean.TRUE) {
            logger.debug("refresh providers for {}", url);
            super.notify(url, listener, urls);  // 刷新，使用本地服务提供者
            return;
        }
        pullParentProviders(url, listener);  // 刷新，尝试从上级获取服务提供者
    }

    @Override
    public List<URL> lookup(URL url) {
        List<URL> urls = super.lookup(url);
        if (!urls.isEmpty()) {
            return urls;
        }
        try {
            List<String> providers = new ArrayList<>();
            for (String path : toCategoriesPath(url)) {
                List<String> children = parentZkClient.getChildren(path);
                if (children != null) {
                    providers.addAll(children);
                }
            }
            return toUrlsWithoutEmpty(url, providers);
        } catch (Throwable e) {
            throw new RpcException("Failed to lookup " + url + " from zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    /*
     * 从上级注册中心的拉取服务提供者，并发出通知。
     */
    private void pullParentProviders(final URL url, final NotifyListener listener) {
        List<URL> urls = super.lookup(url); // 当前注册中心上的服务

        // 判断是否为被禁止中恢复的服务
        if (disabledServices.remove(url) == Boolean.TRUE) {
            logger.debug("Service '{}' has been enabled in current registry", url);
            super.notify(url, listener, urls); // 使用本地服务
            return;
        }

        // 判断是否为被禁止的服务
        if (urls != null && !urls.isEmpty()) {
            Optional<URL> override = findOverrideUrl(urls);
            if (!override.isPresent() || override.get().getParameter(DISABLED_KEY) == null) {
                logger.debug("find providers in current registry: {}", urls);
                super.notify(url, listener, urls);  // 使用本地服务
                return;
            }
            logger.debug("Service '{}' has been disabled in current registry", url);
            disabledServices.put(url, Boolean.TRUE);
        }

        logger.debug("No providers found in current registry, try to pull providers from parent registry for service: {}", url);
        String zkNodePath = toProviderPath(url);

        // 获取上级注册中心的服务提供者
        final DelegateZookeeperRegistry dzr = this;
        ChildListener changeListener = new ChildListener() {
            @Override
            public void childChanged(String path, List<String> children) {
                dzr.notify(url, listener, toUrlsWithEmpty(url, path, children));
            }
        };
        List<String> providers = parentZkClient.addChildListener(zkNodePath, changeListener);
        if (providers == null || providers.isEmpty()) {
            return;
        }

        logger.debug("Find providers from parent registry: {}", providers);
        useParentProviders.put(url, Boolean.TRUE); // 标识使用的是上级注册中心的服务

        // 通知当前注册中心，使其能感知到这些服务提供者
        super.notify(url, listener, toUrlsWithEmpty(url, zkNodePath, providers));
    }

    private Optional<URL> findOverrideUrl(List<URL> urls) {
        for (URL url : urls) {
            if (OVERRIDE_PROTOCOL.equals(url.getProtocol())) {
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (parentZkClient != null) {
            parentZkClient.close();
        }
    }

    private List<URL> toUrlsWithEmpty(URL consumer, String path, List<String> providers) {
        List<URL> urls = toUrlsWithoutEmpty(consumer, providers);
        if (urls.isEmpty()) {
            int i = path.lastIndexOf('/');
            String category = i < 0 ? path : path.substring(i + 1);
            URL empty = consumer.setProtocol(EMPTY_PROTOCOL).addParameter(CATEGORY_KEY, category);
            urls.add(empty);
        }
        return urls;
    }

    private List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList<URL>();
        if (providers != null && !providers.isEmpty()) {
            for (String provider : providers) {
                provider = URL.decode(provider);
                if (provider.contains("://")) {
                    URL url = URL.valueOf(provider);
                    if (UrlUtils.isMatch(consumer, url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

	private String toServicePath(URL url) {
		String name = url.getServiceInterface();
		if (ANY_VALUE.equals(name)) {
			return toRootDir();
		}
		return toRootDir() + URL.encode(name);
	}

	private String toProviderPath(URL url) {
		return toServicePath(url) + PATH_SEPARATOR + DEFAULT_CATEGORY;
	}

}
