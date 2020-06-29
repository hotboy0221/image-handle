package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService3;
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
import java.io.*;

@Service
public class HandleServiceImpl3 implements HandleService3 {
    @Autowired
    private HadoopUtil hadoopUtil;
    private Job job;
    public static int [][]arr;

    @Override
    public void split() throws IOException {
        FileStatus[]fileStatusList=hadoopUtil.getSonPath("image-data/bossbase图片库");
        for(FileStatus fileStatus:fileStatusList){
            StringBuilder sb=new StringBuilder();
            sb.append("split-data/");
            String name=fileStatus.getPath().getName().substring(0,fileStatus.getPath().getName().length()-4);
            sb.append(name);
            Path outputPath=new Path(sb.toString());
            if(hadoopUtil.getFileSystem().exists(outputPath)){
                sb.append(" already exists");
                System.out.println(sb.toString());
                continue;
            }
            //读取图片
            BufferedImage bufferedImage=ImageIO.read(new InputStreamReader(hadoopUtil.getFile(fileStatus.getPath())));
            //
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(hadoopUtil.getFileSystem().create(new Path(sb.toString()))));
            int ki=bufferedReader,kj=256;
            while(ki+kj<1024)
            {
                int splitArr=
                for(int i=ki-256;i<ki;i++){
                    for(int j=kj-256;j<kj;j++){

                    }
                }
                if(ki<=kj){
                    ki+=256;
                }else{
                    kj+=256;
                    ki-=256;
                }
            }
            //            bufferedWriter.write();
            bufferedReader.close();
            bufferedWriter.close();


        }
    }

    @Override
    public synchronized ImageModel partSearch(BufferedImage bufferedImage) throws IOException {
        int [][]arr=new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for(int i=0;i<bufferedImage.getHeight();i++){
            for(int j=0;j<bufferedImage.getWidth();j++){
                int rgb=bufferedImage.getRGB(i, j);
                //计算灰度值
                arr[i][j]=(int) ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3;
            }
        }
        HandleServiceImpl3.arr=arr;
        Job job=Job.getInstance(hadoopUtil.getConfiguration());
        job.setMapperClass(HandleServiceImpl1.mapperHandle.class);
        job.setReducerClass(HandleServiceImpl1.reducerHandle.class);
        job.setInputFormatClass(ImageFileInputFormat.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);


        return null;
    }

    //读入图片，输出小矩阵
    public static class mapperHandle1 extends Mapper<Object,BytesWritable,Object,Object> {
        public void map(Object key, BytesWritable value, Context context) throws IOException {
            String filename=(String)key;
            filename=filename.substring(0,filename.length()-4);
            BufferedImage image= ImageIO.read(new ByteArrayInputStream(value.getBytes()));
            for(int i=0;i<image.getHeight();i++) {
                for (int j = 0; j < image.getWidth(); j++) {

                }
            }

        }

    }
    public static class reducerHandle1 extends Reducer<Object,Object,Object,Object> {

    }
    //输入小矩阵，输出图片名
    public static class mapperHandle2 extends Mapper<Object,Object,Object,Object> {

    }
    public static class reducerHandle2 extends Reducer<Object,Object,Object,Object> {

    }
}
