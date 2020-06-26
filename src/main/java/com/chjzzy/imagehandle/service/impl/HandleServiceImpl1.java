package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService1;
import com.chjzzy.imagehandle.util.HadoopUtil;
import com.chjzzy.imagehandle.util.ImageFileInputFormat;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class HandleServiceImpl1 implements HandleService1 {
    @Autowired
    private HadoopUtil hadoopUtil;


    @PostConstruct
    private void init() throws IOException {

    }

    @Override
    public void count() throws IOException, ClassNotFoundException, InterruptedException {
        FileStatus []fileStatusList=hadoopUtil.getSonPath("image-data/bossbase图片库");


        for(FileStatus fileStatus:fileStatusList){
                    Job job=Job.getInstance(hadoopUtil.getConfiguration());
                    job.setMapperClass(mapperHandle.class);
                    job.setReducerClass(reducerHandle.class);
                    job.setInputFormatClass(ImageFileInputFormat.class);
                    job.setOutputKeyClass(IntWritable.class);
                    job.setOutputValueClass(IntWritable.class);
                    StringBuilder sb=new StringBuilder();
                    sb.append("output-data/");
                    String name=fileStatus.getPath().getName();
                    sb.append(name.substring(0,name.length()-4));
                    Path outputPath=new Path(sb.toString());
                    if(hadoopUtil.getFileSystem().exists(outputPath)){
                        hadoopUtil.getFileSystem().delete(outputPath,true);
                    }
                    //mapreduce
                    FileInputFormat.setInputPaths(job,fileStatus.getPath());
                    FileOutputFormat.setOutputPath(job,outputPath);
                    job.submit();
            }
    }

    public static class mapperHandle extends Mapper<Object, BytesWritable, IntWritable,IntWritable>{
        private IntWritable writeValue=new IntWritable(1);
        private IntWritable writeKey=new IntWritable();
        public void map(Object key, BytesWritable value, Context context) throws IOException,InterruptedException{
            BufferedImage image= ImageIO.read(new ByteArrayInputStream(value.getBytes()));
            int [][]arr=new int[image.getHeight()][image.getWidth()];
            for(int i=0;i<image.getHeight();i++) {
                for(int j=0;j<image.getWidth();j++) {
                    int rgb=image.getRGB(i, j);
                    //计算灰度值
                    arr[i][j]=(int) ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3;
                    //key: pixel  value :totalNum
                    writeKey.set(arr[i][j]);
                    context.write(writeKey,writeValue);
                }
            }

        }
    }
    public static class reducerHandle extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable>{
        private IntWritable writeValue=new IntWritable();
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException,InterruptedException{
            int sum = 0;
            for (IntWritable val : values)
            {
                sum += val.get();
            }
            writeValue.set(sum);
            context.write(key,writeValue);
        }
    }
}
