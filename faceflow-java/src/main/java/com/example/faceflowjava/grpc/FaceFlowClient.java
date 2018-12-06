package com.example.faceflowjava.grpc;

import com.example.facematrix.FaceMatrix;
import com.example.facematrix.FaceTransformGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class FaceFlowClient implements DisposableBean {

    private final ManagedChannel channel = NettyChannelBuilder
            .forAddress("localhost", 50051)
            .negotiationType(NegotiationType.PLAINTEXT)
            .build();
    private final FaceTransformGrpc.FaceTransformBlockingStub blockingStub = FaceTransformGrpc.newBlockingStub(channel);

    @Override
    public void destroy() throws Exception {
        channel.shutdown();
    }

    public FaceMatrix.Matrix getMatrix(FaceMatrix.Face request) {
        return blockingStub.getMatrix(request);
    }
}
