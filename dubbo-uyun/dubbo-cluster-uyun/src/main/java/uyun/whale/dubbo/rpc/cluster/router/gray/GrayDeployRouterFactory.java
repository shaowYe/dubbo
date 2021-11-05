package uyun.whale.dubbo.rpc.cluster.router.gray;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

/**
 * gray deploy router factory
 */
@Activate(order = 200)
public class GrayDeployRouterFactory implements RouterFactory {
    public static final String NAME = "gray";

    private volatile Router router;

    @Override
    public Router getRouter(URL url) {
        if (router != null) {
            return router;
        }
        synchronized (this) {
            if (router == null) {
                router = createRouter(url);
            }
        }
        return router;
    }

    private Router createRouter(URL url) {
        return new GrayDeployRouter(url);
    }
}
