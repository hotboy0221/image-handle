package com.chjzzy.imagehandle.service;

import com.chjzzy.imagehandle.model.ImageModel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface HandleService3 {
    //分割512*512成256*256
    void split() throws IOException;
    List<ImageModel> partSearch(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException;
}
