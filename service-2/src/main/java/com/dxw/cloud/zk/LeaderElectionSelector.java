package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

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

    public void close() { leaderSelector.close(); }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        System.out.println(name + "第"+leaderCount.getAndIncrement()+"次当选为leader!");
        task();
        System.out.println(name + "放弃领导权");
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

    }

    private static CuratorFramework client;

    static {
        String CONNECT_ADDR = Config.zkServer;
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //2 通过工厂创建连接
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .retryPolicy(retryPolicy)
                .build();
        //3 开启连接
        client.start();
    }

    private void task() throws Exception {
        //不带线程池的异步接口
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
