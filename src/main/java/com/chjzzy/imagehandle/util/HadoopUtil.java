package com.chjzzy.imagehandle.util;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.response.BusinessException;
import com.chjzzy.imagehandle.response.EmBusinessError;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
public class HadoopUtil {
    @Value("${hdfs.path}")
    private String path;
    @Value("${hdfs.username}")
    private Configuration configuration;
    private FileSystem fileSystem;
    @PostConstruct
    public void init() throws IOException {
        configuration=new Configuration();
        configuration.set("fs.defaultFS",path);
        configuration.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
        fileSystem=FileSystem.get(configuration);
    }

    /*public ImageModel getImageModel(String path) throws IOException, BusinessException {
        Path file=new Path(path);
        if(fileSystem.exists(file)&&file.getName().matches(".*\\.bmp")){
            InputStream in=fileSystem.open(file);
            BufferedImage image= ImageIO.read(in);
            int [][]arr=new int[image.getHeight()][image.getWidth()];
            for(int i=0;i<image.getHeight();i++) {
                for(int j=0;j<image.getWidth();j++) {
                    int rgb=image.getRGB(i, j);
                    //计算灰度值
                    arr[i][j]=(int) ((rgb&0xff)+((rgb>>8)&0xff)+((rgb>>16)&0xff))/3;
                }
            }
            ImageModel imageModel=new ImageModel();
            imageModel.setName(file.getName());
            imageModel.setPixels(arr);
            return imageModel;
        }else throw new BusinessException(EmBusinessError.Path_Not_Exist);
    }*/
    //获取文件夹下一级中的所有文件路径
    public FileStatus[] getSonPath(String dirPath) throws IOException {
        FileStatus[] fileStatusList=fileSystem.listStatus(new Path(dirPath));
        return fileStatusList;
    }
    public FSDataInputStream getFile(Path path) throws IOException {
        return fileSystem.open(path);
    }
    public Configuration getConfiguration() {
        return configuration;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
