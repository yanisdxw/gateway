package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class LeaderLatchSelector implements LeaderSelect {

    private LeaderLatch leaderLatch;
    private String name;
    private String path;
    private CuratorFramework client;

    public void init(CuratorFramework curatorFramework){
        leaderLatch = new LeaderLatch(curatorFramework, path, name);
        client = curatorFramework;
    }

    public LeaderLatchSelector(String path, String name){
        this.name = name;
        this.path = path;
    };

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
