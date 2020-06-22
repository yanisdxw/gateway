package com.dxw.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Server2Application {

    public static void main(String[] args) {
        SpringApplication.run(Server2Application.class, args);
    }

}
