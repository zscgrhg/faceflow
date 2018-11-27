package com.example.faceflowjava;

import com.example.faceflowjava.grpc.FaceFlowClient;
import com.example.facematrix.FaceMatrix;
import com.google.common.io.Files;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.io.File;

//@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    FaceFlowClient client;

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 1; i++) {

            File hx1 = new File("D:\\AI110\\faceflow\\faceflow-java\\src\\main\\resources\\hx1.jpg");
            ByteString bytes1 = ByteString.copyFrom(Files.toByteArray(hx1));
            FaceMatrix.Face face = FaceMatrix.Face
                    .newBuilder()
                    .setFace(bytes1)
                    .build();
            FaceMatrix.Matrix matrix = client.getMatrix(face);
            System.out.println(matrix);
        }
    }
}
