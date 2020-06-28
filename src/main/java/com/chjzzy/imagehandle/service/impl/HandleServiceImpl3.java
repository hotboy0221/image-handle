package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.service.HandleService3;
import com.chjzzy.imagehandle.util.HadoopUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class HandleServiceImpl3 implements HandleService3 {
    @Autowired
    private HadoopUtil hadoopUtil;
    private Job job;



    public static class mapperHandle extends Mapper<Object,Object,Object,Object> {

    }
    public static class reducerHandle extends Reducer<Object,Object,Object,Object> {

    }
}
