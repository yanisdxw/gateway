package com.dxw.cloud.account;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {

    Account getAccountById(@Param("id") Integer id);

    Account getAccountByNum(@Param("num") String num);

    List<Account> getAllAccounts();

    Integer insertAccount(Account account);

    void updateById(Account account);

}
