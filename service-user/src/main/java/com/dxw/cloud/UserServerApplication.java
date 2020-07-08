package com.dxw.cloud;

import com.dxw.cloud.zk.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(UserServerApplication.class, args);
        ZkClient zkClient = context.getBean(ZkClient.class);
        zkClient.registry("service1", new InstanceDetails("注册服务s3"));
        LeaderElectionSelector leaderSelector = new LeaderElectionSelector("/"+ Config.zkName, "注册服务s3");
        zkClient.tryElection(leaderSelector);
        ServiceListener serviceListener = context.getBean(ServiceListener.class);
        serviceListener.Listen("service1");
        Thread t = new Thread(serviceListener);
        t.start();
    }

}
