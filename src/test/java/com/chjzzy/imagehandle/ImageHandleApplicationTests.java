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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
class ImageHandleApplicationTests {
    @Autowired
    private HadoopUtil hadoopUtil;
    @Autowired
    private HbaseUtil hbaseUtil;
    @Autowired
    private HandleService1 handleService1;



    @Test
    void hadoopTest2() throws IOException, BusinessException {

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
//        System.out.println(new String(hbaseUtil.getData("image","1","statistic","255")));
//        System.out.println(hbaseUtil.existRowKey("image","image111"));
//        System.out.println(hbaseUtil.existRowKey("image","image11"));
//         handleService1.getAllImageModel();


    }
    @Test
    void mapreduceTest() throws InterruptedException, IOException, ClassNotFoundException {
        handleService1.count();
    }
}
