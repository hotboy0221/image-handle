package com.chjzzy.imagehandle.service;

import com.alibaba.fastjson.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface HandleService4 {
    JSONObject alterSearch(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException;
}
