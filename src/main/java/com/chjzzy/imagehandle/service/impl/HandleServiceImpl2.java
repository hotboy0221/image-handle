package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService2;
import com.chjzzy.imagehandle.util.HadoopUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
@Service
public class HandleServiceImpl2 implements HandleService2 {
    @Autowired
    private HadoopUtil hadoopUtil;
    private Job job;
    private static int arr[]=new int[256];


    @Override
    public ImageModel search(BufferedImage bufferedImage) {
        return null;
    }


    public static class mapperHandle extends Mapper<Object,Object,Object,Object> {

        public void map(Object key, Object value, Context context){
            //
            int []arr=HandleServiceImpl2.arr;
            for(int i=0;i<256;i++){
//                if(arr[i]==statistic[i]){}
            }
        }
    }
    public static class reducerHandle extends Reducer<Object,Object,Object,Object> {

    }
}
