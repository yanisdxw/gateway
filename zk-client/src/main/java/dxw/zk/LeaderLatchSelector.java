package dxw.zk;

import lombok.SneakyThrows;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaderLatchSelector extends ZkBaseClient {

    private LeaderLatch leaderLatch;
    private String name;
    private String path;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setPath(String path, String name){
        this.name = name;
        this.path = path;
        leaderLatch = new LeaderLatch(super.getClientInstance(), path, name);
    };

    public LeaderLatchSelector(){

    }

    @SneakyThrows
    public void start(){
        if(leaderLatch==null) return;
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                System.out.println(leaderLatch.getId() +  "当选leader!");
            }

            @Override
            public void notLeader() {
                System.out.println(leaderLatch.getId() +  "不是leader!");
            }
        });
        leaderLatch.start();
    }
}
