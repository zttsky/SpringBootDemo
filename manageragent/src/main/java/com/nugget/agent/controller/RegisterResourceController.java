package com.nugget.agent.controller;

import com.nugget.agent.serivice.ExplorerImpl;
import com.nugget.common.entity.Address;
import com.nugget.heartbeat.entity.CustomProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ExplorerController
 * @Description: 数据承载容器类
 * @Author 米粒儿
 * @Date 2019/9/19
 * @Version V1.0
 **/
@RestController
public class RegisterResourceController {

    @Autowired
    ExplorerImpl explorer;

    /**
     * 暴露注册接口
     * @param customProtocol
     * @return
     */

    @PostMapping(value = "/register")
    public Address registerResource(@RequestBody CustomProtocol customProtocol){
        return explorer.registerResource(customProtocol);
    }


    /**
     * 获取最小值
     * @param channel
     * @return
     */
    @GetMapping(value = "/searchCount")
    public int searchCount(@RequestParam(defaultValue = "", value = "channel") String channel){
        return explorer.searchCount(channel);
    }

}
