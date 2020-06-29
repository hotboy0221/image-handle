package com.chjzzy.imagehandle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService4;
import com.chjzzy.imagehandle.util.HadoopUtil;
import com.chjzzy.imagehandle.util.HbaseUtil;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class HandleServiceImpl4 implements HandleService4 {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private HbaseUtil hbaseUtil;
    //存储搜索图片的数据
    public static int [][]searchArray;
    //存储搜索结果
    private static JSONObject result=new JSONObject();

    @Override
    public synchronized JSONObject alterSearch(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException {
        int [][]arr=new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for(int i=0;i<bufferedImage.getHeight();i++){
            for(int j=0;j<bufferedImage.getWidth();j++){
                int rgb=bufferedImage.getRGB(i, j);
                //计算灰度值
                arr[i][j]=(int) ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3;
            }
        }
        searchArray=arr;
        Path outputPath=new Path("question4");
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
       ;
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader( hadoopUtil.getFile(new Path("question4"))));
        String[] strs=bufferedReader.readLine().split("\t");
        result.put("name", strs[0]);
        result.put("bytecode",hbaseUtil.getData("image",(String)result.get("name"),"info","bytecode"));
        List<Point>pointList=new ArrayList<>();

        result.put("alterPoints",pointList);
        result.clear();
        return result;
    }
    public static class Point{
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
    public static class mapperHandle extends Mapper<LongWritable, Text,Text,Text> {
        int sum=Integer.MAX_VALUE;
        StringBuilder index=null;
        String fileName=null;
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
                    int k= notMatch(validateArray,searchArray,i,j,sum);
                    if(sum>k){
                         sum=k;
                         index=new StringBuilder();
                         index.append(sum);
                         index.append("\t");
                         index.append(i);
                         index.append("\t");
                         index.append(j);
                         fileName=inputFileName;
                    }
                }
            }
            context.write(new Text(inputFileName),new Text(index.toString()));
          
        }
        @Override
        protected void cleanup(Mapper<LongWritable,Text,Text, Text>.Context context)
                throws IOException, InterruptedException {
            context.write(new Text(fileName), new Text(index.toString()));
        }


        //查找不匹配的点
        private  int  notMatch(int[][] validateArr,int [][]searchArr,int offsetI,int offsetJ,int max){
            int sum=0;
            for(int i=0;i<searchArr.length;i++){
                for(int j=0;j<searchArr.length;j++){
                    if(searchArr[i][j]!=validateArr[i+offsetI][j+offsetJ]){
                        sum++;
                        if(sum>=max){
                            return Integer.MAX_VALUE;
                        }
                    }
                }
            }
            return sum;
        }
    }
    public static class reducerHandle extends Reducer<Text,Text,Text,Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for(Text text:values){
                context.write(key,text);
            }
        }
    }
}
