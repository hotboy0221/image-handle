package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService2;
import com.chjzzy.imagehandle.util.HadoopUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

public class HandleServiceImpl2 implements HandleService2 {
    @Autowired
    private HadoopUtil hadoopUtil;
    private Job job;
    @PostConstruct
    private void init() throws IOException {
        job=Job.getInstance(hadoopUtil.getConfiguration());
        job.setMapperClass(HandleServiceImpl1.mapperHandle.class);
        job.setReducerClass(HandleServiceImpl1.reducerHandle.class);
    }


    public static class mapperHandle extends Mapper<Object,Object,Object,Object> {

    }
    public static class reducerHandle extends Reducer<Object,Object,Object,Object> {

    }
}
