package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService1;
import com.chjzzy.imagehandle.util.HadoopUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class HandleServiceImpl1 implements HandleService1 {
    @Autowired
    private HadoopUtil hadoopUtil;
    private Job job;
    @PostConstruct
    private void init() throws IOException {
        job=Job.getInstance(hadoopUtil.getConfiguration());
        job.setMapperClass(mapperHandle.class);
        job.setReducerClass(reducerHandle.class);
    }

    @Override
    public List<ImageModel> count() {
        return null;
    }

    public static class mapperHandle extends Mapper<Object,Object,Object,Object>{

    }
    public static class reducerHandle extends Reducer<Object,Object,Object,Object>{

    }
}
