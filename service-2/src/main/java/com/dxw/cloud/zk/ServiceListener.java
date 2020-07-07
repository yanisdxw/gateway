package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ServiceListener implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String zookeeperServer;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int baseSleepTimeMs;
    private int maxRetries;
    private String providerName;

    private ServiceDiscovery<InstanceDetails> serviceDiscovery;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    private static CuratorFramework client = null;

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }
    public String getZookeeperServer() {
        return zookeeperServer;
    }
    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }
    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }
    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }
    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    public int getMaxRetries() {
        return maxRetries;
    }

    @SneakyThrows
    private void init(){
        String CONNECT_ADDR = zookeeperServer;
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        //2 通过工厂创建连接
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .retryPolicy(retryPolicy)
                .build();
        //3 开启连接
        client.start();
        client.blockUntilConnected();

        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(client)
                .basePath(Config.name)
                .serializer(new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class))
                .build();
        serviceDiscovery.start();
    }

    public void Listen(String name){
        setProviderName(name);
    }

    public void stop() {
        client.close();
        logger.info("已断开zk链接！");
    }

    @SneakyThrows
    @Override
    public void run() {
        boolean flag = true;

        //死循环来不停的获取服务列表,查看是否有新服务发布
        while(flag){
            //根据名称获取服务
            Collection<ServiceInstance<InstanceDetails>> services = serviceDiscovery.queryForInstances(providerName);
            if(services.size() == 0){
                System.out.println("当前没有发现服务");
                Thread.sleep(10 * 1000);
                continue;
            }
            for(ServiceInstance<InstanceDetails> service : services) {
                //获取请求的scheme 例如：http://127.0.0.1:8080
                String uriSpec = service.buildUriSpec();
                //获取服务的其他信息
                InstanceDetails payload = service.getPayload();
                //服务描述
                String serviceDesc = payload.getServiceDesc();
                System.out.println(serviceDesc);
            }
            System.out.println("---------------------");
        }
    }
}
