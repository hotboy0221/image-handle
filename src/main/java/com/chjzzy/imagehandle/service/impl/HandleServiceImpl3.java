package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService3;
import com.chjzzy.imagehandle.util.HadoopUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.IOException;
@Service
public class HandleServiceImpl3 implements HandleService3 {
    @Autowired
    private HadoopUtil hadoopUtil;
    private Job job;

    @Override
    public ImageModel partSearch(BufferedImage bufferedImage) {
        int [][]arr=new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for(int i=0;i<bufferedImage.getHeight();i++){
            for(int j=0;j<bufferedImage.getWidth();j++){
                int rgb=bufferedImage.getRGB(i, j);
                //计算灰度值
                arr[i][j]=(int) ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3;
            }
        }
        return null;
    }


    public static class mapperHandle extends Mapper<Object,Object,Object,Object> {

    }
    public static class reducerHandle extends Reducer<Object,Object,Object,Object> {

    }
}
