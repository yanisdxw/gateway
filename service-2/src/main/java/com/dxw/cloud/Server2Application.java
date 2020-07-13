package com.dxw.cloud;

import com.dxw.cloud.zk.*;
import dxw.zk.LeaderElectionSelector;
import dxw.zk.ServiceListener;
import dxw.zk.ZkClient;
import dxw.zk.bean.InstanceDetails;
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
        zkClient.registry(Config.name, Config.host, Config.port, new InstanceDetails("注册服务s2"));
        //选举
        LeaderElectionSelector leaderElectionSelector = context.getBean(LeaderElectionSelector.class);
        leaderElectionSelector.setPath("/"+ Config.name, "注册服务s2");
        leaderElectionSelector.setTask(context.getBean(ZkTask.class));
        leaderElectionSelector.start();
        //监听
        ServiceListener serviceListener = context.getBean(ServiceListener.class);
        serviceListener.Listen(Config.name);
        Thread t = new Thread(serviceListener);
        t.start();
    }

}
