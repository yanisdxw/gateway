package com.dxw.cloud.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserServiceFeignDao userServiceFeignDao;

    @RequestMapping(value = "/ping")
    public String ping(String hello){
        return hello;
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public String save(
            @RequestParam Integer id,
            @RequestParam Double input){
        return userServiceFeignDao.save(id,input);
    }

    @RequestMapping(value = "/draw",method = RequestMethod.POST)
    public String draw(
            @RequestParam Integer id,
            @RequestParam Double output){
        return userServiceFeignDao.draw(id,output);
    }


}
