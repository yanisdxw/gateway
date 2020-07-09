package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class LeaderLatchSelector implements LeaderSelect {

    private LeaderLatch leaderLatch;
    private String name;
    private String path;

    public void init(CuratorFramework curatorFramework){
        leaderLatch = new LeaderLatch(curatorFramework, path, name);
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
            }

            @Override
            public void notLeader() {
                System.out.println(leaderLatch.getId() +  "不是leader!");
            }
        });
        leaderLatch.start();
    }
}
