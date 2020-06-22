package com.dxw.cloud.account;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    @Autowired
    AccountMapper accountMapper;

    @Transactional
    public Double saveMoney(Integer id, Double input){
        try {
            Account account = accountMapper.getAccountById(id);
            account.save(input);
            accountMapper.updateById(account);
            return account.balance;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Transactional
    public Double drawMoney(Integer id, Double output){
        try {
            Account account = accountMapper.getAccountById(id);
            account.draw(output);
            accountMapper.updateById(account);
            return account.balance;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    public Integer createAccount(Account account){
        Integer id = accountMapper.insertAccount(account);
        return id;
    }

}
