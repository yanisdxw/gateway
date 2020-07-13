package dxw.zk;

import dxw.zk.bean.InstanceDetails;
import lombok.SneakyThrows;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ServiceListener extends ZkBaseClient implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String providerName;

    private ServiceDiscovery<InstanceDetails> serviceDiscovery;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @SneakyThrows
    public void Listen(String name){
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(getClientInstance())
                .basePath(name)
                .serializer(new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class))
                .build();
        serviceDiscovery.start();
        setProviderName(name);
    }

    public void stop() {
        getClientInstance().close();
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
                System.out.println(serviceDesc+"-"+uriSpec);
            }
            Thread.sleep(1 * 1000);
            System.out.println("---------------------");
        }
    }
}
