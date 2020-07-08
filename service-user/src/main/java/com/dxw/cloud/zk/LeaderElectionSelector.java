package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.atomic.AtomicInteger;

public class LeaderElectionSelector implements LeaderSelectorListener, LeaderSelect {

    /** leaderSelector */
    private LeaderSelector leaderSelector;
    private String name;
    private String path;
    /** 原子性的 用来记录获取 leader的次数 */
    public AtomicInteger leaderCount = new AtomicInteger(1);

    public void init(CuratorFramework curatorFramework){
        leaderSelector = new LeaderSelector(curatorFramework, path, this);
        /**
         * 自动重新排队
         * 该方法的调用可以确保此实例在释放领导权后还可能获得领导权
         */
        leaderSelector.autoRequeue();
    }

    public LeaderElectionSelector(String path, String name){
        this.name = name;
        this.path = path;
    };

    /**
     * 启动  调用leaderSelector.start()
     */
    @SneakyThrows
    public void start() {
        leaderSelector.start();
    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        System.out.println(name + "第"+leaderCount.getAndIncrement()+"次当选为leader!");
        Thread.sleep(1*1000);
        System.out.println(name + "放弃领导权");
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

    }

    private void task(CuratorFramework curatorFramework) throws Exception {

    }

}
