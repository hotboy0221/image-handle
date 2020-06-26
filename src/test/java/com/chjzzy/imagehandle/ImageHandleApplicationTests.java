package com.chjzzy.imagehandle;

import com.chjzzy.imagehandle.model.ImageModel;
import com.chjzzy.imagehandle.response.BusinessException;
import com.chjzzy.imagehandle.service.HandleService1;
import com.chjzzy.imagehandle.service.impl.HandleServiceImpl1;
import com.chjzzy.imagehandle.util.HadoopUtil;
import com.chjzzy.imagehandle.util.HbaseUtil;
import org.apache.hadoop.fs.FileStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ImageHandleApplicationTests {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private HbaseUtil hbaseUtil;
    @Autowired
    private HandleService1 handleService1;


    @Test
    void hadoopTest() throws IOException, BusinessException {
        ImageModel imageModel =hadoopUtil.getImageModel("image-data/bossbase图片库/1.bmp");
        int [][]arr=imageModel.getPixels();
        for(int i=0;i<arr.length;i++){
            System.out.println();
            for(int j=0;j<arr[i].length;j++){
                System.out.print(arr[i][j]+" ");
            }
        }
    }
    @Test
    void getAllImagePath() throws IOException {
        FileStatus[] fileStatuses=hadoopUtil.getSonPath("image-data/bossbase图片库");
        System.out.println(fileStatuses[0].getPath().getName());
        System.out.println(fileStatuses[2].getPath().getName());

    }
    @Test
    void hbaseTest() throws IOException {
//        hbaseUtil.insertData("image","image111","info","bytecode","hahahaha");
//        hbaseUtil.insertData("image","image111","statistic","2","100");
        System.out.println(hbaseUtil.getData("image","image111","info","bytecode"));
    }
    @Test
    void mapreduceTest() throws InterruptedException, IOException, ClassNotFoundException {
        handleService1.count();
    }
}
