package com.dxw.cloud.zk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class zkConfiguration {

    @Value("${zkServer.server}")
    private String zookeeperServer;
    @Value(("${zkServer.sessionTimeoutMs}"))
    private int sessionTimeoutMs;
    @Value("${zkServer.connectionTimeoutMs}")
    private int connectionTimeoutMs;
    @Value("${zkServer.maxRetries}")
    private int maxRetries;
    @Value("${zkServer.baseSleepTimeMs}")
    private int baseSleepTimeMs;

    @Bean(initMethod = "init", destroyMethod = "stop")
    public ZkClient zkClient() {
        ZkClient zkClient = new ZkClient();
        zkClient.setZookeeperServer(zookeeperServer);
        zkClient.setSessionTimeoutMs(sessionTimeoutMs);
        zkClient.setConnectionTimeoutMs(connectionTimeoutMs);
        zkClient.setMaxRetries(maxRetries);
        zkClient.setBaseSleepTimeMs(baseSleepTimeMs);
        return zkClient;
    }

    @Bean(initMethod = "init", destroyMethod = "stop")
    public ServiceListener serviceListener(){
        ServiceListener serviceListener = new ServiceListener();
        serviceListener.setZookeeperServer(zookeeperServer);
        serviceListener.setSessionTimeoutMs(sessionTimeoutMs);
        serviceListener.setConnectionTimeoutMs(connectionTimeoutMs);
        serviceListener.setMaxRetries(maxRetries);
        serviceListener.setBaseSleepTimeMs(baseSleepTimeMs);
        return serviceListener;
    }
}
