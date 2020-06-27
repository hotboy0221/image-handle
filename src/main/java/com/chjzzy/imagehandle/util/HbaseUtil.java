package com.chjzzy.imagehandle.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class HbaseUtil {
    private Configuration configuration;
    private Connection connection;
    private Admin admin;
    @Value("${hbase.rootdir}")
    private String rootdir;
    @PostConstruct
    public void init() throws IOException {
        configuration=HBaseConfiguration.create();
        configuration.set("hbase.rootdir",rootdir);
        connection = ConnectionFactory.createConnection(configuration);
        admin = connection.getAdmin();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
                try {
                    if(admin != null){
                    admin.close();
                    }
                    if(null != connection){
                        connection.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }));
        //
        createTable("image","info","statistic");
    }
    /*
     *表名 image
     *
     *行键			|	                  列族
     *		        |     文件信息info   		统计信息statistic
     *文件名filename	|	图片字节码bytecode 	0-255像素的个数 0,1,2....
     *
     *
     * */
    private boolean createTable(String name,String... colFamily) throws IOException {
        TableName tableName=TableName.valueOf(name);
        if(admin.tableExists(tableName)){
            return false;
        }else{
            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableName);
            for(String str:colFamily){
                ColumnFamilyDescriptor columnFamilyDescriptor =
                        ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(str)).build();
                tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
            }
            admin.createTable(tableDescriptorBuilder.build());
            return true;
        }
    }

    public  void insertData(String tableName,String rowKey,String colFamily,String col,byte[] val) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(rowKey.getBytes());
        put.addColumn(colFamily.getBytes(),col.getBytes(), val);
        table.put(put);
        table.close();
    }

    public  byte[] getData(String tableName,String rowKey,String colFamily, String col)throws  IOException{
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        get.addColumn(colFamily.getBytes(),col.getBytes());
        byte[] result = table.get(get).getValue(colFamily.getBytes(),col.getBytes());
        table.close();
        return result;
    }
    public boolean existRowKey(String tableName,String rowKey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get=new Get(rowKey.getBytes());
        boolean isExist=table.exists(get);
        table.close();
        return isExist;
    }



}
