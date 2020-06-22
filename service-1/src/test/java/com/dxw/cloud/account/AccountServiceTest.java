package com.dxw.cloud.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountMapper accountMapper;

    @Test
    public void test(){
        Account account = new Account();
        account.setName("yanis");
        account.setNum("136057");
        account.setBalance(10000D);
        accountService.createAccount(account);
    }

    @Test
    public void test2(){
        Account account = accountMapper.getAccountById(1);
        System.out.println(account);
    }

    @Test
    public void test3(){
        Double balance = accountService.drawMoney(2,500d);
        System.out.println("left:"+balance);
    }

}