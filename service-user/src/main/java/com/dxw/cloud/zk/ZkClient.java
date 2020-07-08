package com.dxw.cloud.zk;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZkClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static CuratorFramework client = null;
    private static ServiceRegistry serviceRegistrar = null;

    private String zookeeperServer;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int baseSleepTimeMs;
    private int maxRetries;

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

    @PreDestroy
    private void close(){
        client.close();
    }

    @SneakyThrows
    public void createNode(String path, String nodeData){
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,nodeData.getBytes());
    }

    @SneakyThrows
    public String getNodeData(String path){
        return new String(client.getData().forPath(path));
    }

    @SneakyThrows
    public void setNodeData(String path, String nodeData){
        client.setData().forPath(path, nodeData.getBytes());
    }

    @SneakyThrows
    public void delNode(String path){
        client.delete().inBackground().forPath(path);
    }


    //1.path Cache  连接  路径  是否获取数据
    //能监听所有的字节点 且是无限监听的模式 但是 指定目录下节点的子节点不再监听
    @SneakyThrows
    public void setPathCacheListenter(String path) {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (PathChildrenCache childrenCache = new PathChildrenCache(client, path, true)) {
            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    System.out.println("开始进行事件分析:-----");
                    ChildData data = event.getData();
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("CHILD_ADDED : " + data.getPath() + "  数据:" + data.getData());
                            break;
                        case CHILD_REMOVED:
                            System.out.println("CHILD_REMOVED : " + data.getPath() + "  数据:" + data.getData());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("CHILD_UPDATED : " + data.getPath() + "  数据:" + data.getData());
                            break;
                        default:
                            break;
                    }
                }
            };
            childrenCache.getListenable().addListener(childrenCacheListener);
            logger.info("Register zk watcher successfully!");
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        }
    }

    //2.Node Cache  监控本节点的变化情况   连接 目录 是否压缩
    //监听本节点的变化  节点可以进行修改操作  删除节点后会再次创建(空节点)
    @SneakyThrows
    public void setNodeCacheListenter(String path) {
        ExecutorService pool = Executors.newCachedThreadPool();
        //设置节点的cache
        final NodeCache nodeCache = new NodeCache(client, path, false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                logger.info("the test node is change and result is :");
                logger.info("path : "+nodeCache.getCurrentData().getPath());
                logger.info("data : "+new String(nodeCache.getCurrentData().getData()));
                logger.info("stat : "+nodeCache.getCurrentData().getStat());
            }
        });
        nodeCache.start();
    }


    //3.Tree Cache  
    // 监控 指定节点和节点下的所有的节点的变化--无限监听  可以进行本节点的删除(不在创建)
    @SneakyThrows
    public void setTreeCacheListenter(String path) {
        ExecutorService pool = Executors.newCachedThreadPool();
        //设置节点的cache
        TreeCache treeCache = new TreeCache(client, path);
        //设置监听器和处理过程
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if(data !=null){
                    switch (event.getType()) {
                        case NODE_ADDED:
                            logger.info("NODE_ADDED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                            break;
                        case NODE_REMOVED:
                            logger.info("NODE_REMOVED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                            break;
                        case NODE_UPDATED:
                            logger.info("NODE_UPDATED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                            break;

                        default:
                            break;
                    }
                }else{
                    logger.info( "data is null : "+ event.getType());
                }
            }
        });
        //开始监听
        treeCache.start();
    }

    public void registry(String name, InstanceDetails instanceDetails){
        try {
            if(serviceRegistrar==null){
                serviceRegistrar = new ServiceRegistry(client, Config.zkName);
            }
            ServiceInstance<InstanceDetails> instance = ServiceInstance.<InstanceDetails>builder()
                    .name(name)
                    .port(Config.port)
                    .address(Config.host)   //address不写的话，会取本地ip
                    .payload(instanceDetails)
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .build();
            serviceRegistrar.registerService(instance);
        }catch (Exception e){
            logger.error("注册失败",e);
        }
    }

    public void unregister(String name){
        try {
            serviceRegistrar.unregisterService(name);
        }catch (Exception e){
            logger.error("取消注册失败",e);
        }
    }

    public void tryElection(LeaderSelect leaderSelect){
        leaderSelect.init(client);
        leaderSelect.start();
    }

    public void stop() {
        serviceRegistrar.unregisterService();
        client.close();
        logger.info("已断开zk链接！");
    }

    public CuratorFramework getClient() {
        return client;
    }
}
