package com.chjzzy.imagehandle.service;

import com.chjzzy.imagehandle.model.ImageModel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HandleService1 {
    //读取所有的图片，统计各自的像素值，存进文件
    void count() throws IOException, ClassNotFoundException, InterruptedException;

    //获取所有图片信息
    List<ImageModel> getAllImageModel(int page) throws IOException;
}
