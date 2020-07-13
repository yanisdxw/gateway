package com.dxw.cloud.zk;

import dxw.zk.bean.InstanceTask;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

@Service
public class ZkTask implements InstanceTask {

    private CuratorFramework client = null;

    @Override
    public void init(CuratorFramework client) {
        this.client = client;
    }

    @SneakyThrows
    @Override
    public void run() {
        if(client==null) return;
        Stat stat = client.checkExists().forPath("/common");
        if(stat==null){
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/common","0".getBytes());
        }else {
            String rst = new String(client.getData().forPath("/common"));
            System.out.println(rst);
            int count = Integer.valueOf(rst);
            count ++;
            client.setData().forPath("/common", String.valueOf(count).getBytes());
            Thread.sleep(1*1000);
        }
    }
}
