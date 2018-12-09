package com.example.faceflowjava.action;

import com.example.faceflowjava.beandefs.HBaseClient;
import com.example.faceflowjava.grpc.FaceFlowClient;
import com.example.facematrix.FaceEntry;
import com.example.facematrix.FaceMatrix;
import com.google.protobuf.ByteString;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faces")
public class FacenetController {
    @Autowired
    FaceFlowClient faceFlowClient;
    @Autowired
    HBaseClient hBaseClient;

    private static double[] unwrap(FaceMatrix.Matrix matrix) {
        return matrix
                .getMatrixList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    private static double[] unwrap(FaceEntry.Entry matrix) {
        return matrix
                .getMatrixList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    @RequestMapping("/index")
    public String index(Map model) {
        model.putIfAbsent("message", "Facenet meets Tensorflow!");
        model.putIfAbsent("matches", "");
        return "faces/index";
    }

    @RequestMapping("/add")
    public String add(@RequestParam("face1") MultipartFile face1,
                      @NotBlank @RequestParam(value = "employId", required = true) String employId,
                      RedirectAttributes redirectAttributes) throws IOException {
        byte[] bytes = face1.getBytes();
        FaceMatrix.Matrix matrix1 = getMatrix(bytes);

        String tstamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        hBaseClient.addFace(employId, tstamp, bytes, matrix1);


        redirectAttributes.addFlashAttribute("message",
                "successfully!");
        return "redirect:/faces/index";
    }

    @RequestMapping("/identify")
    public String identify(@RequestParam("face") MultipartFile face,
                           @NotBlank @RequestParam(value = "employId", required = true) String employId,
                           RedirectAttributes redirectAttributes) throws IOException {
        FaceMatrix.Matrix matrix = getMatrix(face.getBytes());
        if (true) {
            boolean exist = hBaseClient.exist(matrix);
            redirectAttributes.addFlashAttribute("matches", exist);
        } else {
            List<FaceMatrix.Matrix> matrices = hBaseClient.find(employId);
            boolean matches = matrices
                    .stream()
                    .allMatch(t -> {
                        double compute = new EuclideanDistance().compute(
                                unwrap(t), unwrap(matrix));
                        return compute < 1.0;
                    });
            redirectAttributes.addFlashAttribute("matches", matches);
        }

        return "redirect:/faces/index";
    }

    private FaceMatrix.Matrix getMatrix(byte[] file) {
        FaceMatrix.Face face = FaceMatrix.Face
                .newBuilder()
                .setFace(ByteString.copyFrom(file))
                .build();
        return faceFlowClient.getMatrix(face);
    }
}
