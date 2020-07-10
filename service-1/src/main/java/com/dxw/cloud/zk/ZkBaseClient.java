package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public abstract class ZkBaseClient {

    private String zookeeperServer;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int baseSleepTimeMs;
    private int maxRetries;
    private static CuratorFramework client = null;

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }
    public String getZookeeperServer() {
        return zookeeperServer;
    }
    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }
    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }
    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }
    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    public int getMaxRetries() {
        return maxRetries;
    }

    @SneakyThrows
    private void init(){
        String CONNECT_ADDR = zookeeperServer;
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        //2 通过工厂创建连接
        client = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .retryPolicy(retryPolicy)
                .build();
        //3 开启连接
        client.start();
        client.blockUntilConnected();
    }

    public CuratorFramework getClientInstance(){
        return client;
    }
}
