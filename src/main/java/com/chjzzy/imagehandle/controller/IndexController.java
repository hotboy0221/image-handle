package com.chjzzy.imagehandle.controller;

import com.chjzzy.imagehandle.response.CommonReturnType;
import com.chjzzy.imagehandle.service.HandleService1;
import com.chjzzy.imagehandle.service.HandleService2;
import com.chjzzy.imagehandle.service.HandleService3;
import com.chjzzy.imagehandle.service.HandleService4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@CrossOrigin()
@RestController
@RequestMapping(value = "/imagehandle")
public class IndexController {
    @Autowired
    private HandleService1 handleService1;
    @Autowired
    private HandleService2 handleService2;
    @Autowired
    private HandleService3 handleService3;
    @Autowired
    private HandleService4 handleService4;
    @RequestMapping(value = "/dofirst")
    public CommonReturnType doFirst(@RequestParam(name = "page")int page) throws IOException, InterruptedException, ClassNotFoundException {
        handleService1.count();
        return CommonReturnType.create(handleService1.getAllImageModel(page));
    }

    @RequestMapping(value = "/dosecond")
    public CommonReturnType doSecond(@RequestParam(name="image")MultipartFile image) throws IOException {

        return CommonReturnType.create(handleService2.search(ImageIO.read(image.getInputStream())));
    }

    @RequestMapping(value = "/dothird")
    public CommonReturnType doThird(@RequestParam(name="image")MultipartFile image) throws IOException {

        return CommonReturnType.create(handleService3.partSearch(ImageIO.read(image.getInputStream())));
    }

    @RequestMapping(value = "/doforth")
    public CommonReturnType doForth(@RequestParam(name="image")MultipartFile image){

        return CommonReturnType.create(null);
    }
}
