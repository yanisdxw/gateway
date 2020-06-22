package com.dxw.cloud.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "account")
public interface UserServiceFeignDao {

    @RequestMapping(value = "/account/save",method = RequestMethod.POST)
    String save(@RequestParam("id") Integer id,
                @RequestParam("input") Double input);

    @RequestMapping(value = "/account/draw",method = RequestMethod.POST)
    String draw(@RequestParam("id") Integer id,
                @RequestParam("output") Double output);
}
