package com.dxw.cloud.account;

import lombok.Data;

import java.util.Date;

@Data
public class Account {
    public String name;
    public String num;
    public Integer id;
    public Date createTime;
    public Double balance;

    public Double save(Double input){
        balance += input;
        return balance;
    }

    public Double draw(Double output) throws Exception{
        balance -= output;
        if(balance<0){
            throw new Exception("no enough money!");
        }
        return balance;
    }
}
