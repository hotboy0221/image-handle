package com.chjzzy.imagehandle.service;

import com.chjzzy.imagehandle.model.ImageModel;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface HandleService2 {
    ImageModel search(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException;
}
