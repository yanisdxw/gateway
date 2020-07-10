package com.dxw.cloud;

import com.dxw.cloud.zk.*;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class Server2Application {

    @SneakyThrows
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Server2Application.class, args);
        //注册
        ZkClient zkClient = context.getBean(ZkClient.class);
        zkClient.registry("service1", new InstanceDetails("注册服务s2"));
        //选举
        LeaderElectionSelector leaderElectionSelector = context.getBean(LeaderElectionSelector.class);
        leaderElectionSelector.setPath("/"+ Config.zkName, "注册服务s2");
        leaderElectionSelector.start();
        //监听
        ServiceListener serviceListener = context.getBean(ServiceListener.class);
        serviceListener.Listen("service1");
        Thread t = new Thread(serviceListener);
        t.start();
    }

}
