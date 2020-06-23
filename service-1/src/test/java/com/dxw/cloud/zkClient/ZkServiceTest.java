package com.dxw.cloud.zkClient;

import com.dxw.cloud.Server1Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Server1Application.class)
public class ZkServiceTest {

    @Autowired
    ZkService zkService;

    @Test
    public void test1() throws Exception {
        zkService.createNode("/test1","hello world");
        String ret = zkService.getNodeData("/test1");
        System.out.println(ret);
        Thread.sleep(5*1000);
        zkService.delNode("/test1");
    }

    @Test
    public void test2() throws Exception {
        zkService.createNode("/test1","hello world");
        String ret = zkService.getNodeData("/test1");
        System.out.println(ret);
        zkService.setPathCacheListenter("/test1");
        boolean flag = false; String last=null;
        while (!flag){
            String recall = zkService.getNodeData("/test1");
            if(!recall.equals(last)){
                System.out.println(recall);
                last = recall;
            }
            if("stop".equals(recall)) flag=true;
        }
        zkService.delNode("/test1");
        System.out.println("ALL STOP!");
        Thread.sleep(5*1000);
    }

    @Test
    public void test3() throws Exception {
        zkService.createNode("/test1","hello world");
        String ret = zkService.getNodeData("/test1");
        System.out.println(ret);
        zkService.setTreeCacheListenter("/test1");
        boolean flag = false; String last=null;
        while (!flag){
            String recall = zkService.getNodeData("/test1");
            if(!recall.equals(last)){
                System.out.println(recall);
                last = recall;
            }
            if("stop".equals(recall)) flag=true;
        }
        zkService.delNode("/test1");
        System.out.println("ALL STOP!");
        Thread.sleep(5*1000);
    }

}