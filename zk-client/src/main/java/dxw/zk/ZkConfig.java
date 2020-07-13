package dxw.zk;

import dxw.zk.utils.YamlUtils;
import java.util.Map;

public class ZkConfig {
    /** zk节点名称*/
    public String zkName;
    /** zk服务地址*/
    public String zkServer;
    public int sessionTimeoutMs;
    public int connectionTimeoutMs;
    public int maxRetries;
    public int baseSleepTimeMs;

    private static ZkConfig zkConfigInstance;

    private ZkConfig(){}

    private ZkConfig(Map<String, Object> zkConfig, Map<String, Object> serverConfig){
        zkName = (String) zkConfig.get("name");
        zkServer = (String) zkConfig.get("server");
        sessionTimeoutMs = (int) zkConfig.get("sessionTimeoutMs");
        connectionTimeoutMs = (int) zkConfig.get("connectionTimeoutMs");
        maxRetries = (int) zkConfig.get("maxRetries");
        baseSleepTimeMs = (int) zkConfig.get("baseSleepTimeMs");
    };

    public static synchronized ZkConfig getInstance(){
        if(zkConfigInstance==null){
            Map<String, Object> zookeeperConfig = YamlUtils.getResMap("zkServer");
            Map<String, Object> serverConfig = YamlUtils.getResMap("server");
            zkConfigInstance = new ZkConfig(zookeeperConfig, serverConfig);
        }
        return zkConfigInstance;
    }

}
