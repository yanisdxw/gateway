package com.dxw.cloud.zk;

import dxw.zk.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class zkConfiguration {

    @Bean(initMethod = "init", destroyMethod = "stop")
    public ZkClient zkClient() {
        ZkClient zkClient = new ZkClient();
        zkClient.setZookeeperConfig(ZkConfig.getInstance());
        return zkClient;
    }

    @Bean(initMethod = "init", destroyMethod = "stop")
    public ServiceListener serviceListener(){
        ServiceListener serviceListener = new ServiceListener();
        serviceListener.setZookeeperConfig(ZkConfig.getInstance());
        return serviceListener;
    }

    @Bean(initMethod = "init")
    public LeaderElectionSelector leaderElectionSelector(){
        LeaderElectionSelector leaderElectionSelector = new LeaderElectionSelector();
        leaderElectionSelector.setZookeeperConfig(ZkConfig.getInstance());
        return leaderElectionSelector;
    }

    @Bean(initMethod = "init")
    public LeaderLatchSelector leaderLatchSelector(){
        LeaderLatchSelector leaderLatchSelector = new LeaderLatchSelector();
        leaderLatchSelector.setZookeeperConfig(ZkConfig.getInstance());
        return leaderLatchSelector;
    }
}
