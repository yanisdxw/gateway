package com.dxw.cloud.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private static Map<String, ServiceInstance> serviceInstanceMap = new ConcurrentHashMap<>();

    public ServiceRegistry(CuratorFramework client, String basePath) throws Exception {
        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(
                InstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class).client(client).serializer(serializer)
                .basePath(basePath).build();
        serviceDiscovery.start();
    }

    public void registerService(ServiceInstance<InstanceDetails> serviceInstance) throws Exception {
        serviceDiscovery.registerService(serviceInstance);
        serviceInstanceMap.put(serviceInstance.getName(),serviceInstance);
    }

    public void unregisterService(ServiceInstance<InstanceDetails> serviceInstance) throws Exception {
        serviceDiscovery.unregisterService(serviceInstance);
        serviceInstanceMap.remove(serviceInstance.getName());
    }

    public void unregisterService(String name) throws Exception {
        ServiceInstance<InstanceDetails> serviceInstance = serviceInstanceMap.get(name);
        if(serviceInstance!=null){
            serviceDiscovery.unregisterService(serviceInstance);
            serviceInstanceMap.remove(serviceInstance.getName());
        }
    }

    public void unregisterService() {
        serviceInstanceMap.forEach((k,v)-> {
            try {
                serviceDiscovery.unregisterService(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateService(ServiceInstance<InstanceDetails> serviceInstance) throws Exception {
        serviceDiscovery.updateService(serviceInstance);
        serviceInstanceMap.put(serviceInstance.getName(), serviceInstance);
    }

    public void close() throws IOException {
        serviceInstanceMap.clear();
        serviceDiscovery.close();
    }
}
