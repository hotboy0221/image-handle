package com.chjzzy.imagehandle.controller;


import com.chjzzy.imagehandle.response.CommonReturnType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice(basePackages = "com.chizzy.imagehandle")
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonReturnType doError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Exception ex) {
        Map<String,Object> responseData = new HashMap<>();
        ex.printStackTrace();
        responseData.put("errCode","500");
        responseData.put("errMsg",ex.getCause());
        return CommonReturnType.create(responseData,false);
    }
}
