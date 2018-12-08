package com.example.faceflowjava.beandefs;

import lombok.experimental.Delegate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HBaseClient implements DisposableBean, Connection {
    @Delegate
    private final Connection connection = create();

    public final Connection create() {
        try {
            Configuration conf = HBaseConfiguration.create();
            return ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        connection.close();
    }
}
