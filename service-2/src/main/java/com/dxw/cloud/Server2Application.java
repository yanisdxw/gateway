package com.dxw.cloud;

import com.dxw.cloud.zk.ZkService;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class Server2Application {

    @SneakyThrows
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Server2Application.class, args);
        ZkService zkService = context.getBean(ZkService.class);
        zkService.registry("service1");
    }

}
