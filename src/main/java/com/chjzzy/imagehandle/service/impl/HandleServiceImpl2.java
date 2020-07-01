package com.chjzzy.imagehandle.service.impl;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.service.HandleService2;
import com.chjzzy.imagehandle.util.HadoopUtil;
import com.chjzzy.imagehandle.util.HbaseUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class HandleServiceImpl2 implements HandleService2 {
    @Autowired
    private HadoopUtil hadoopUtil;

    private static int []searchArray;


    @Override
    public synchronized ImageModel search(BufferedImage bufferedImage) throws IOException, ClassNotFoundException, InterruptedException {
        searchArray=new int[256];
        for(int i=0;i<bufferedImage.getHeight();i++){
            for(int j=0;j<bufferedImage.getWidth();j++){
                int rgb=bufferedImage.getRGB(i, j);
                //计算直方图
                searchArray[ ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3]++;
            }
        }
        Job job=Job.getInstance(hadoopUtil.getConfiguration());
        job.setMapperClass(mapperHandle.class);
        job.setReducerClass(reducerHandle.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        Scan scan=new Scan();
        scan.setCaching(1000);
        scan.setCacheBlocks(false);
        TableMapReduceUtil.initTableMapperJob(
                "image", // input // table
                scan, // Scan instance to control CF and attribute selection
                mapperHandle.class, // mapper class
                Text.class, // mapper output key
                Text.class, // mapper output value
                job);
        Path outputPath=new Path("question2");
        if(hadoopUtil.getFileSystem().exists(outputPath)){
            hadoopUtil.getFileSystem().delete(outputPath,true);
        }
        FileOutputFormat.setOutputPath(job,outputPath);
        job.waitForCompletion(true);
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(hadoopUtil.getFile(new Path("question2/part-r-00000"))));
        String line=bufferedReader.readLine();
        if(line==null)return null;
        String []strs=line.split("\t");
        ImageModel imageModel=new ImageModel();
        imageModel.setName(strs[0]);
        imageModel.setBytecode(strs[1]);
        bufferedReader.close();

        return imageModel;
    }


    public static class mapperHandle extends TableMapper<Text, Text> {
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            int []arr=new int[256];
            String bytecode=null;
            String filename=null;
            for(Cell cell:value.rawCells()){
                if("statistic".equals(Bytes.toString(CellUtil.cloneFamily(cell)))){
                    arr[Integer.parseInt(Bytes.toString(CellUtil.cloneQualifier(cell)))]= Integer.parseInt(Bytes.toString(CellUtil.cloneValue(cell)));
                }else if("bytecode".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))){
                    bytecode=Bytes.toString(CellUtil.cloneValue(cell));
                    filename=Bytes.toString(CellUtil.cloneRow(cell));
                }
            }
            for(int i=0;i<arr.length;i++){
                if(arr[i]!=searchArray[i])return;
            }
            context.write(new Text(filename),new Text(bytecode));
        }
    }
    public static class reducerHandle extends Reducer<Text,Text,Text,Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value:values){
                context.write(key,value);
            }
        }
    }
}
