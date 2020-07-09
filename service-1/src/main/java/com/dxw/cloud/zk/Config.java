package com.dxw.cloud.zk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;

@Configuration
public class Config {

    /** 本节点主机 */
    public static String host;
    /** 本节点端口 */
    public static int port;
    /** spring节点名称*/
    public static String name;
    /** zk节点名称*/
    public static String zkName;
    /** zk服务地址*/
    public static String zkServer;

    private static Environment Env;

    @Autowired
    public void setEnv(Environment env) {
        Config.Env = env;
    }

    @PostConstruct
    private void init() {
        try {
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new Error(e);
        }
        port = Integer.valueOf(Env.getProperty("server.port"));
        name = Env.getProperty("spring.application.name");
        zkName = Env.getProperty("zkServer.name");
        zkServer = Env.getProperty("zkServer.server");
    }
}
