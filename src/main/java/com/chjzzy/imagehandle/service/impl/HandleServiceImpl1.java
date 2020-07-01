package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService1;
import com.chjzzy.imagehandle.util.HadoopUtil;
import com.chjzzy.imagehandle.util.HbaseUtil;
import com.chjzzy.imagehandle.util.ImageFileInputFormat;
import com.chjzzy.imagehandle.util.SpringContextUtil;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.List;

@Service
public class HandleServiceImpl1 implements HandleService1 {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private HbaseUtil hbaseUtil;
    //mapper
    public static class mapperHandle extends Mapper<Object, BytesWritable, IntWritable,IntWritable>{
    //图片以字节读取
        private IntWritable writeValue=new IntWritable(1);
        private IntWritable writeKey=new IntWritable();
        public void map(Object key, BytesWritable value, Context context) throws IOException,InterruptedException{  //key:文件名；value：文件内容
            HbaseUtil hbaseUtil= (HbaseUtil) SpringContextUtil.getBean("hbaseUtil");
            BufferedImage image= ImageIO.read(new ByteArrayInputStream(value.getBytes()));
            //获取图片灰度值矩阵
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
            //将数据插入到Hbase
            String filename=(String)key;
            filename=filename.substring(0,filename.length()-4);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", stream);
            hbaseUtil.insertData("image",filename,"info","bytecode",new String(Base64.getEncoder().encode(stream.toByteArray())).getBytes());
            stream.close();
        }
    }
    //reducer
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

    @Override
    public void count() throws IOException, ClassNotFoundException, InterruptedException {
        FileStatus []fileStatusList=hadoopUtil.getSonPath("image-data/bossbase图片库");
        for(FileStatus fileStatus:fileStatusList){
            StringBuilder sb=new StringBuilder();
            sb.append("output-data/");
            String name=fileStatus.getPath().getName().substring(0,fileStatus.getPath().getName().length()-4);
            sb.append(name);
            Path outputPath=new Path(sb.toString());
            if(hadoopUtil.getFileSystem().exists(outputPath)){
                sb.append(" already exists");
                System.out.println(sb.toString());
                continue;
            }
            Job job=Job.getInstance(hadoopUtil.getConfiguration());
            job.setMapperClass(mapperHandle.class);
            job.setReducerClass(reducerHandle.class);
            job.setInputFormatClass(ImageFileInputFormat.class); //设置输入类型
            job.setOutputKeyClass(IntWritable.class);  //设置输出类型
            job.setOutputValueClass(IntWritable.class);
            //mapreduce
            FileInputFormat.setInputPaths(job,fileStatus.getPath());
            FileOutputFormat.setOutputPath(job,outputPath);
            job.waitForCompletion(true);
            //hbase 插入统计数据
            //输出结果在output-data/图片名/part-r-00000路径下
            sb.append("/part-r-00000");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(hadoopUtil.getFile(new Path(sb.toString()))));
            String line=null;
            while((line=bufferedReader.readLine())!=null){
                String[] strs=line.split("\t");
                hbaseUtil.insertData("image",name,"statistic",strs[0],strs[1].getBytes());
            }
            bufferedReader.close();
        }
    }

    @Override
    public List<ImageModel> getAllImageModel(int page) throws IOException {
        FileStatus[]fileStatuses=hadoopUtil.getSonPath("output-data/");
        return hbaseUtil.getAllImageModel(fileStatuses,page,50);
    }


}
