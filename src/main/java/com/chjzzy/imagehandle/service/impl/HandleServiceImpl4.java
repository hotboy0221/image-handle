package com.chjzzy.imagehandle.service.impl;

import com.alibaba.fastjson.JSONObject;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@Service
public class HandleServiceImpl4 implements HandleService4 {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private HbaseUtil hbaseUtil;
    //存储搜索图片的数据
    public static int [][]searchArray;


    @Override
    public synchronized JSONObject alterSearch(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException {
        //存储搜索结果
        JSONObject result=new JSONObject();
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
        //read result
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader( hadoopUtil.getFile(outputPath)));
        String line=null;
        String pointStr=null;
        int max=Integer.MAX_VALUE;
        while((line=bufferedReader.readLine())!=null){
            String[] strs=bufferedReader.readLine().split("\t");
            int k=Integer.valueOf(strs[1]);
            if(k<max){
                result.put("name",strs[0]);
                max=k;
                pointStr=strs[1];
            }
        }

        result.put("bytecode",hbaseUtil.getData("image",(String)result.get("name"),"info","bytecode"));

        String []point=pointStr.split("-");
        List<Point>pointList=new LinkedList<>();
        for(int i=0;i+1<point.length;i+=2){
            pointList.add(new Point(Integer.valueOf(point[i]),Integer.valueOf(point[i+1])));
        }
        result.put("alterPoints",pointList);
        bufferedReader.close();
        return result;
    }
    public static class Point{
        private int row;//row_index
        private int col;//col_index

        public Point(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }
    }
    public static class mapperHandle extends Mapper<LongWritable, Text,Text,Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            int sum=Integer.MAX_VALUE;
            List<Point>alterPoints=null;
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
                    List<Point> pointList= notMatch(validateArray,searchArray,i,j,sum);
                    if(pointList!=null&&sum>pointList.size()){
                         sum=pointList.size();
                         alterPoints=pointList;
                    }
                }
            }
            context.write(new Text(inputFileName),new Text(convertToString(alterPoints)));
        }


        //查找不匹配的点
        private  List<Point>  notMatch(int[][] validateArr,int [][]searchArr,int offsetI,int offsetJ,int max){
            List<Point> pointList=new LinkedList<>();
            for(int i=0;i<searchArr.length;i++){
                for(int j=0;j<searchArr.length;j++){
                    if(searchArr[i][j]!=validateArr[i+offsetI][j+offsetJ]){
                        pointList.add(new Point(i,j));
                        if(pointList.size()>=max){
                            return null;
                        }
                    }
                }
            }
            return pointList;
        }
        private String convertToString(List<Point> points){
            StringBuilder sb=new StringBuilder();
            sb.append(points.size());
            sb.append("\t");
            for(Point point:points){
                sb.append(point.getRow());
                sb.append("-");
                sb.append(point.getCol());
                sb.append("-");
            }
            return sb.toString();
        }

    }
    public static class reducerHandle extends Reducer<Text,Text,Text,Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Text value=null;
            int max=Integer.MAX_VALUE;
            for(Text text:values){
                int k=Integer.valueOf(text.toString().split("\t")[0]);
                if(k<max){
                    max=k;
                    value=text;
                }
            }
            context.write(key,value);
        }
    }
}
