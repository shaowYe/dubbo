package uyun.whale.dubbo.registry;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DelegateZookeeperRegistryFactory.
 *
 * @author hesy
 */
public class DelegateZookeeperRegistryFactory extends AbstractRegistryFactory {
  private static final Logger logger = LoggerFactory.getLogger(DelegateZookeeperRegistryFactory.class);
  private ZookeeperTransporter zookeeperTransporter;

  public DelegateZookeeperRegistryFactory() {
    this.zookeeperTransporter = ZookeeperTransporter.getExtension();
  }

  
  @Override
  public Registry createRegistry(URL url) {
    String delegateZkUrl = url.getParameter("delegate");
    URL parentZKUrl = null;
    if (delegateZkUrl != null) {
      url = url.removeParameter("delegate");
      String[] hostPorts = delegateZkUrl.split(":");
      parentZKUrl = createDelegateURL(url, hostPorts[0], hostPorts[1]);
      logger.info("using delegate : {}", parentZKUrl);
    } 
    return (Registry) new DelegateZookeeperRegistry(url, parentZKUrl, this.zookeeperTransporter);
  }
  
  private URL createDelegateURL(URL url, String host, String port) {
    String _host = (host != null) ? host : url.getHost();
    int _port = (port != null) ? Integer.parseInt(port) : url.getPort();
    return new URL(url.getProtocol(), url.getUsername(), url.getPassword(), _host, _port, url.getPath(), url.getParameters());
  }
}
