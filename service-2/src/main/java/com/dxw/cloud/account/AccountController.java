package com.dxw.cloud.account;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @RequestMapping(value="/save", method = RequestMethod.POST)
    public String save(
            @RequestParam Integer id,
            @RequestParam Double input){
        return accountService.saveMoney(id,input)+" done by 9002";
    }

    @RequestMapping(value = "/draw",method = RequestMethod.POST)
    public String draw(
            @RequestParam Integer id,
            @RequestParam Double output
    ){
        return accountService.drawMoney(id,output)+" done by 9002";
    }

}
