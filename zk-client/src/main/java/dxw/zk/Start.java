package dxw.zk;

public class Start {
    public static void main(String[] args) {
        ZkConfig zkConfig = ZkConfig.getInstance();
        System.out.println(zkConfig.zkServer);
    }
}
