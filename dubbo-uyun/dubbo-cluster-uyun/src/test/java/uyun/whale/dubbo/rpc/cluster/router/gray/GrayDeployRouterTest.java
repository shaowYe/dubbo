package uyun.whale.dubbo.rpc.cluster.router.gray;

import org.apache.dubbo.common.URL;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.support.MockInvoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GrayDeployRouterTest {

    /**
     * 消费方 非灰, 提供方全灰
     */
    @Test
    public void testRoute_ConsumerNotGray(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p")
                .addParameter("gray.version", "1.0.0"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p")
                .addParameter("gray.version", "1.0.1"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p")
                .addParameter("gray.version", "1.0.2"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(invokers, filteredInvokers);
    }

    /**
     * 消费方 灰度, 提供方全部非灰
     */
    @Test
    public void testRoute_ProviderNotGray(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c").addParameter("gray.version", "1.0.0");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(invokers, filteredInvokers);
    }

    /**
     * 消费方灰度, 提供方优先匹配一致版本
     */
    @Test
    public void testRoute_VersionMatch(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c")
                .addParameter("gray.version", "1.0.0");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p")
                .addParameter("gray.version", "1.0.0"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p")
                .addParameter("gray.version", "1.0.1"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p")
                .addParameter("gray.version", "1.0.0"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(filteredInvokers.size(), 2);
        Assertions.assertEquals(filteredInvokers.get(0), invokers.get(0));
        Assertions.assertEquals(filteredInvokers.get(1), invokers.get(2));
    }

    /**
     * 消费方灰度, 提供方优先匹配一致版本
     */
    @Test
    public void testRoute_InnerVersionMatchFound(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c")
                .addParameter("gray.version", "1.0.0");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "c")
                .addParameter("gray.version", "1.0.1"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "c")
                .addParameter("gray.version", "1.0.0"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "c")
                .addParameter("gray.version", "1.0.2"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "c")
                .addParameter("gray.version", "1.0.0"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(filteredInvokers.size(), 2);
        Assertions.assertEquals(filteredInvokers.get(0), invokers.get(1));
        Assertions.assertEquals(filteredInvokers.get(1), invokers.get(3));
    }

    /**
     * 消费方灰度, 提供方优先匹配一致版本失败时, 匹配最高版本
     */
    @Test
    public void testRoute_MaxVersionMatch(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c")
                .addParameter("gray.version", "1.0.5");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d")
                .addParameter("gray.version", "1.0.1"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d")
                .addParameter("gray.version", "1.0.2"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d")
                .addParameter("gray.version", "1.0.0"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d")
                .addParameter("gray.version", "1.0.2"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(filteredInvokers.size(), 2);
        Assertions.assertEquals(filteredInvokers.get(0), invokers.get(1));
        Assertions.assertEquals(filteredInvokers.get(1), invokers.get(3));
    }


    /**
     * 消费方非灰, 提供方部分是灰度, 匹配非灰节点
     */
    @Test
    public void testRoute_PartMatch(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d")
                .addParameter("gray.version", "1.0.1"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d")
                .addParameter("gray.version", "1.0.2"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "d"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(filteredInvokers.size(), 2);
        Assertions.assertEquals(filteredInvokers.get(0), invokers.get(1));
        Assertions.assertEquals(filteredInvokers.get(1), invokers.get(3));
    }

    /**
     * 消费方 非灰, 提供方全非灰
     */
    @Test
    public void testRoute_AllNotGray(){
        URL consumerURL = URL.valueOf("consumer://10.20.3.3:20880/com.foo.BarService").addParameter("product", "c");

        List<URL> providerList = new ArrayList<URL>();
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p"));
        providerList.add(URL.valueOf("dubbo://10.20.3.3:20880/com.foo.BarService")
                .addParameter("product", "p"));

        List<Invoker<String>> invokers = new ArrayList<Invoker<String>>();
        for(URL url : providerList) {
            invokers.add(new MockInvoker<String>(url, String.class));
        }

        Router router = new GrayDeployRouter(consumerURL);

        List<Invoker<String>> filteredInvokers = router.route(invokers, consumerURL, new RpcInvocation());
        Assertions.assertEquals(invokers, filteredInvokers);
    }
}