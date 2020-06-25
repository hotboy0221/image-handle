package com.chjzzy.imagehandle.controller;

import com.chjzzy.imagehandle.response.CommonReturnType;
import com.chjzzy.imagehandle.service.HandleService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/imagehandle")
public class IndexController {
    @Autowired
    private HandleService1 handleService1;
    @RequestMapping(value = "/dofirst")
    public CommonReturnType doFirst(){
        return CommonReturnType.create(handleService1.count());
    }

    @RequestMapping(value = "/dosecond")
    public CommonReturnType doSecond(){

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/dothird")
    public CommonReturnType doThird(){

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/doforth")
    public CommonReturnType doForth(){

        return CommonReturnType.create(null);
    }
}
