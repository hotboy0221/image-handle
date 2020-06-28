package com.chjzzy.imagehandle.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;


import java.io.IOException;

public class ImageFileInputFormat extends FileInputFormat<Object, BytesWritable> {

    @Override
    protected boolean isSplitable(JobContext context, Path filename){
        return false;//保证单图不被分割
    }
    @Override
    public RecordReader<Object, BytesWritable> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new ImageRecordReader();
    }

    public static class ImageRecordReader extends RecordReader<Object, BytesWritable>{
        private Object key=null;
        private BytesWritable value=null;
        private FSDataInputStream fileStream=null;
        private FileSplit filesplit;
        private boolean processed= false;
        private Configuration conf;

        @Override
        public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            filesplit = (FileSplit)inputSplit;
            conf= taskAttemptContext.getConfiguration();
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            if(!processed){
                Path filePath =filesplit.getPath();
                FileSystem fs= filePath.getFileSystem(conf);

                this.fileStream= fs.open(filePath);
                this.key=filePath.getName();
                byte[] bytes =new byte[(int) filesplit.getLength()];
                IOUtils.readFully(this.fileStream, bytes, 0, bytes.length);

                this.value= new BytesWritable(bytes);
                IOUtils.closeStream(fileStream);
                processed =true;
                return true;
            }
            return false;
        }

        @Override
        public Object getCurrentKey() throws IOException, InterruptedException {
            return key;
        }

        @Override
        public BytesWritable getCurrentValue() throws IOException, InterruptedException {
            return value;
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return processed ? 1.0f : 0.0f;
        }

        @Override
        public void close() throws IOException {

        }
    }
}
