package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ProviderService {

    @Autowired
    ZkService zkService;

    @Value("${server.port}")
    private Integer port;

    @SneakyThrows
    @PostConstruct
    private void init(){

        InetAddress address = InetAddress.getLocalHost();

        //服务构造器
        ServiceInstanceBuilder<InstanceDetails> sib = ServiceInstance.builder();
        //该服务中所有的接口
        Map<String,InstanceDetails.Service> services = new HashMap<>();

        // 添加订单服务接口
        //服务所需要的参数
        ArrayList<String> addOrderParams = new ArrayList<>();
        addOrderParams.add("createTime");
        addOrderParams.add("state");

        InstanceDetails.Service addOrderService = new InstanceDetails.Service();
        addOrderService.setDesc("添加订单");
        addOrderService.setMethodName("addOrder");
        addOrderService.setParams(addOrderParams);
        services.put("addOrder",addOrderService);


        //服务的其他信息
        InstanceDetails payload = new InstanceDetails();
        payload.setServiceDesc("订单服务");
        payload.setServices(services);

        //将服务添加到 ServiceInstance
        ServiceInstance<InstanceDetails> orderService = sib.address(address.getHostAddress())
                .port(port)
                .name("OrderService")
                .payload(payload)
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build();

        //构建 ServiceDiscovery 用来注册服务
        ServiceDiscovery<InstanceDetails> serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(zkService.getClientInstance())
                .serializer(new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class))
                .basePath(InstanceDetails.ROOT_PATH)
                .build();
        //服务注册
        serviceDiscovery.registerService(orderService);
        serviceDiscovery.start();
        System.out.println("第一台服务注册成功......");
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        serviceDiscovery.close();
    }

}
