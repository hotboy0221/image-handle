package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.service.HandleService4;
import com.chjzzy.imagehandle.util.HadoopUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class HandleServiceImpl4 implements HandleService4 {
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
