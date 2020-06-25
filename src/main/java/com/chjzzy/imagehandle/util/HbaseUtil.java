package com.chjzzy.imagehandle.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
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
    }

    private void createTable(){

    }


}
