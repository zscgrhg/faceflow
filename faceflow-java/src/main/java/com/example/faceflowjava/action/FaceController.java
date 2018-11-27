package com.example.faceflowjava.action;

import com.example.faceflowjava.grpc.FaceFlowClient;
import com.example.facematrix.FaceMatrix;
import com.google.protobuf.ByteString;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
public class FaceController {

    @Autowired
    FaceFlowClient client;

    @RequestMapping("/index")
    public String index(Map model) {
        model.putIfAbsent("message", "Facenet meets Tensorflow!");
        model.putIfAbsent("distance", "");
        return "index";
    }

    @PostMapping("/compare")
    public String handleFileUpload(@RequestParam("face1") MultipartFile face1,
                                   @RequestParam("face2") MultipartFile face2,
                                   RedirectAttributes redirectAttributes) throws IOException {


        FaceMatrix.Matrix matrix1 = getMatrix(face1.getBytes());
        FaceMatrix.Matrix matrix2 = getMatrix(face2.getBytes());
        double[] doubles1 = matrix1
                .getMatrixList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double[] doubles2 = matrix2
                .getMatrixList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double distance = new EuclideanDistance().compute(doubles1, doubles2);
        redirectAttributes.addFlashAttribute("message",
                "successfully!");
        redirectAttributes.addFlashAttribute("distance",
                distance);
        return "redirect:/index";
    }

    private FaceMatrix.Matrix getMatrix(byte[] file) {
        FaceMatrix.Face face = FaceMatrix.Face
                .newBuilder()
                .setFace(ByteString.copyFrom(file))
                .build();
        return client.getMatrix(face);
    }

}
