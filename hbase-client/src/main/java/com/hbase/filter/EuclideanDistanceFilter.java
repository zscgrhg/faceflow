package com.hbase.filter;

import com.example.facematrix.FaceMatrix;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class EuclideanDistanceFilter extends FilterBase {
    private final double[] matrix;
    private boolean ignored = true;


    public EuclideanDistanceFilter(double[] matrix) {
        this.matrix = matrix;
    }

    //@Override
    public static Filter parseFrom(final byte[] pbBytes)
            throws DeserializationException {
        try {
            double[] doubles = FaceMatrix.Matrix
                    .parseFrom(pbBytes)
                    .getMatrixList()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            return new EuclideanDistanceFilter(doubles);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reset() throws IOException {
        this.ignored = true;
    }

    @Override
    public ReturnCode filterKeyValue(Cell cell) throws IOException {
        byte[] valueArray = cell.getValueArray();
        double[] mx = FaceMatrix.Matrix
                .parseFrom(valueArray)
                .getMatrixList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double compute = new EuclideanDistance().compute(matrix, mx);
        if (compute < 1) {
            this.ignored = false;
        }
        return ReturnCode.INCLUDE;
    }


    @Override
    public boolean filterRow() throws IOException {
        return ignored;
    }

    @Override
    public boolean hasFilterRow() {
        return true;
    }

    @Override
    public byte[] toByteArray() {


        FaceMatrix.Matrix matrix = FaceMatrix.Matrix
                .newBuilder()
                .addAllMatrix(DoubleStream
                        .of(this.matrix)
                        .mapToObj(Double::valueOf)
                        .collect(Collectors.toList()))
                .build();

        return matrix.toByteArray();
    }
}
