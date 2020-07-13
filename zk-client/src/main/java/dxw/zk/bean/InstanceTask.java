package dxw.zk.bean;

import org.apache.curator.framework.CuratorFramework;

public interface InstanceTask {
    void init(CuratorFramework client);
    void run() throws Exception;
}
