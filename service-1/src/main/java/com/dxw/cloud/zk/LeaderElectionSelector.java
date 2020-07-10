package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderElectionSelector extends ZkBaseClient implements LeaderSelectorListener {

    /** leaderSelector */
    private LeaderSelector leaderSelector;
    private String name;
    private String path;
    /** 原子性的 用来记录获取 leader的次数 */
    public AtomicInteger leaderCount = new AtomicInteger(1);
    /**执行任务的异步线程池*/
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setPath(String path, String name){
        this.name = name;
        this.path = path;
        leaderSelector = new LeaderSelector(super.getClientInstance(),  path, this);
        /**
         * 自动重新排队
         * 该方法的调用可以确保此实例在释放领导权后还可能获得领导权
         */
        leaderSelector.autoRequeue();
    };

    public LeaderElectionSelector(){};

    /**
     * 启动  调用leaderSelector.start()
     */
    @SneakyThrows
    public void start() {
        leaderSelector.start();
    }

    @Override
    public void takeLeadership(CuratorFramework cf) throws Exception {
        System.out.println(name + "第"+leaderCount.getAndIncrement()+"次当选为leader!");
        task();
        System.out.println(name + "放弃领导权");

    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

    }

    private void task() throws Exception {
        CuratorFramework client = super.getClientInstance();
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
