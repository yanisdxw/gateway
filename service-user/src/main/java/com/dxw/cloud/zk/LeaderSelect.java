package com.dxw.cloud.zk;

import org.apache.curator.framework.CuratorFramework;

public interface LeaderSelect {
    void init(CuratorFramework curatorFramework);
    void start();
}
