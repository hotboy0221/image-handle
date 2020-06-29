package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService3;
import com.chjzzy.imagehandle.util.HadoopUtil;
import com.chjzzy.imagehandle.util.HbaseUtil;
import com.chjzzy.imagehandle.util.ImageFileInputFormat;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

@Service
public class HandleServiceImpl3 implements HandleService3 {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private HbaseUtil hbaseUtil;
    //存储搜索图片的数据
    public static int [][]searchArray;
    //存储搜索结果
    public static List<String> fileNameList=new LinkedList<>();
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
            BufferedImage bufferedImage=ImageIO.read(hadoopUtil.getFile(fileStatus.getPath()));

            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(hadoopUtil.getFileSystem().create(new Path(sb.toString()))));
            //将图片分成4个矩阵
            int li=bufferedImage.getHeight()/2,lj=bufferedImage.getWidth()/2;
            int ki=0,kj=0;
            while(ki+kj<3)
            {
                StringBuilder splitArr=new StringBuilder();
                int offsetI=ki*li;
                int offsetJ=kj*lj;
                for(int i=0;i<li;i++){
                    for(int j=0;j<lj;j++){
                        int rgb=bufferedImage.getRGB(i+offsetI, j+offsetJ);
                        //计算灰度值
                        splitArr.append(((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3);
                        splitArr.append(" ");
                    }
                    splitArr.append("\t");
                }
                if(ki<=kj){
                    ki++;
                }else{
                    kj++;
                    ki--;
                }
                splitArr.append("\n");
                bufferedWriter.write(splitArr.toString());
            }
            bufferedWriter.close();
        }
    }

    @Override
    public synchronized List<ImageModel> partSearch(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException {
        int [][]arr=new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for(int i=0;i<bufferedImage.getHeight();i++){
            for(int j=0;j<bufferedImage.getWidth();j++){
                int rgb=bufferedImage.getRGB(i, j);
                //计算灰度值
                arr[i][j]=(int) ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3;
            }
        }
        searchArray=arr;
        Path outputPath=new Path("question3");
        if(hadoopUtil.getFileSystem().exists(outputPath)){
            hadoopUtil.getFileSystem().delete(outputPath,true);
        }
        FileStatus[]fileStatusList=hadoopUtil.getSonPath("split-data");
        Job job = Job.getInstance(hadoopUtil.getConfiguration());
        job.setMapperClass(HandleServiceImpl3.mapperHandle.class);
        job.setReducerClass(HandleServiceImpl3.reducerHandle.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job,outputPath);
        for(FileStatus fileStatus:fileStatusList) {
            FileInputFormat.addInputPath(job,fileStatus.getPath());
        }
        job.waitForCompletion(true);
        List<ImageModel>imageModelList=hbaseUtil.getImageModelListByRowKey(fileNameList,false);
        fileNameList.clear();
        return imageModelList;
    }


    public static class mapperHandle extends Mapper<LongWritable, Text,Text,Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //获取256*256的部分图来验证
            String inputFileName=((FileSplit)context.getInputSplit()).getPath().getName();
            String[] partArrays=value.toString().split("\t");
            int [][]validateArray=new int[partArrays.length][];
            for(int i=0; i<partArrays.length;i++){
                String[]x=partArrays[i].split(" ");
                validateArray[i]=new int[x.length];
                for(int j=0;j<x.length;j++){
                    validateArray[i][j]=Integer.valueOf(x[j]);
                }
            }
            int matchI=validateArray.length-searchArray.length;
            int matchJ=validateArray[0].length-searchArray[0].length;
            for(int i=0;i<=matchI;i++){
                for(int j=0;j<matchJ;j++){
                    if(match(validateArray,searchArray,i,j)){
                        context.write(new Text(inputFileName),new Text(""));
                    }
                }
            }
        }
        private  boolean match(int[][] validateArr,int [][]searchArr,int offsetI,int offsetJ){
            for(int i=0;i<searchArr.length;i++){
                for(int j=0;j<searchArr.length;j++){
                    if(searchArr[i][j]!=validateArr[i+offsetI][j+offsetJ])return false;
                }
            }
            return true;
        }
    }
    public static class reducerHandle extends Reducer<Text,Text,Text,Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            fileNameList.add(key.toString());
        }
    }

}
