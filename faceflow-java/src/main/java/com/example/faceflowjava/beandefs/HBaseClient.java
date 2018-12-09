package com.example.faceflowjava.beandefs;

import com.example.facematrix.FaceMatrix;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.experimental.Delegate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.stream.Collectors;

@Component
public class HBaseClient implements DisposableBean, Connection {
    @Delegate
    private final Connection connection = create();

    public final Connection create() {
        try {
            Configuration conf = HBaseConfiguration.create();
            Connection connection = ConnectionFactory.createConnection(conf);
            initTables(connection);
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addFace(String employId,
                        String id,
                        byte[] face,
                        FaceMatrix.Matrix matrix) throws IOException {
        Table table = connection.getTable(TableName.valueOf("default:facesmatrix"));
        Put putObj = new Put(employId.getBytes());
        //上面行指定完后,就需要指定列族,列,值,这样才是完整的
        putObj.addColumn("face".getBytes(), id.getBytes(),
                face);
        putObj.addColumn("matrix".getBytes(),
                id.getBytes(),
                matrix.toByteArray());
        //执行~
        table.put(putObj);
    }

    public List<FaceMatrix.Matrix> find(String employId) throws IOException {
        Table table = connection.getTable(TableName.valueOf("default:facesmatrix"));
        Get get = new Get(employId.getBytes());
        get.addFamily("matrix".getBytes());
        Result result = table.get(get);
        NavigableMap<byte[], byte[]> familyMap =
                result.getFamilyMap("matrix".getBytes());
        List<FaceMatrix.Matrix> matrices = familyMap
                .values()
                .stream()
                .map(this::parse)
                .collect(Collectors.toList());
        return matrices;
    }

    private FaceMatrix.Matrix parse(byte[] data) {
        try {
            return FaceMatrix.Matrix.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }

    private void initTables(Connection connection) throws IOException {
        Admin admin = connection.getAdmin();
        //创建表名描述
        TableName tableName = TableName.valueOf("default:facesmatrix");
        //创建表描述,并赋予表名
        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder
                .newBuilder(tableName);
        //Builder构建列族信息
        ColumnFamilyDescriptor faceid = ColumnFamilyDescriptorBuilder
                .newBuilder("face".getBytes())
                .setInMemory(false)
                .build();
        ColumnFamilyDescriptor matrixid = ColumnFamilyDescriptorBuilder
                .newBuilder("matrix".getBytes())
                .setInMemory(true)
                .build();
        //将列族信息也赋予表描述对象
        TableDescriptor table = tableBuilder
                .setColumnFamilies(Arrays.asList(faceid, matrixid))
                .build();
        //创建表
        boolean tableExists = admin.tableExists(tableName);
        if (!tableExists) {
            admin.createTable(table);
        }
    }

    @Override
    public void destroy() throws Exception {
        connection.close();
    }
}
