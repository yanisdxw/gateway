package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaderLatchSelector extends LeaderSelect {

    private LeaderLatch leaderLatch;
    private String name;
    private String path;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static CuratorFramework client = null;

    @SneakyThrows
    private void init(){
        String CONNECT_ADDR = super.getZookeeperServer();
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(super.getBaseSleepTimeMs(), super.getMaxRetries());
        //2 通过工厂创建连接
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .retryPolicy(retryPolicy)
                .build();
        //3 开启连接
        client.start();
        client.blockUntilConnected();
    }

    public void setPath(String path, String name){
        this.name = name;
        this.path = path;
        leaderLatch = new LeaderLatch(client, path, name);
    };

    public LeaderLatchSelector(){

    }

    @SneakyThrows
    public void start(){
        if(leaderLatch==null) return;
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                System.out.println(leaderLatch.getId() +  "当选leader!");
                try {
                    System.out.println(leaderLatch.getId() +  "当选leader!");
                    Stat stat = client.checkExists().forPath("/common");
                    if(stat==null){
                        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/common","0".getBytes());
                    }else {
                        String rst = new String(client.getData().forPath("/common"));
                        System.out.println(rst);
                        int count = Integer.valueOf(rst);
                        count ++;
                        client.setData().forPath("/common", String.valueOf(count).getBytes());
                    }
                    leaderLatch.close(LeaderLatch.CloseMode.NOTIFY_LEADER);
                    Thread.sleep(1*1000);
                }catch (Exception e){

                }finally {

                }
            }

            @Override
            public void notLeader() {
                System.out.println(leaderLatch.getId() +  "不是leader!");
            }
        });
        leaderLatch.start();
    }
}
