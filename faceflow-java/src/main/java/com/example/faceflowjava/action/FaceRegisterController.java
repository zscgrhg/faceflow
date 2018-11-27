package com.example.faceflowjava.action;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Controller
@RequestMapping("/register")
public class FaceRegisterController {
    public static final String DB_BASE = "D:\\AI110\\faceflow\\faceflow-java\\src\\main\\resources\\db";
    @Autowired
    FaceFlowClient client;

    public static final Map<String, List<FaceEntry.Entry>> dbCache = new ConcurrentHashMap<>();

    static {
        File[] files = new File(DB_BASE).listFiles();
        for (File file : files) {
            try {
                FaceEntry.Entry entry = FaceEntry.Entry
                        .parseFrom(new FileInputStream(file));
                addEntryToDbCache(entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @RequestMapping("/index")
    public String index(Map model) {
        model.putIfAbsent("message", "Facenet meets Tensorflow!");
        model.putIfAbsent("matches", "");
        return "register/index";
    }

    @RequestMapping("/add")
    public String add(@RequestParam("face1") MultipartFile face1,
                      @NotBlank @RequestParam(value = "name",required = true) String name,
                      RedirectAttributes redirectAttributes) throws IOException {
        FaceMatrix.Matrix matrix1 = getMatrix(face1.getBytes());
        double[] doubles1 = matrix1
                .getMatrixList()
                .stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        List<Double> data = DoubleStream
                .of(doubles1)
                .boxed()
                .collect(Collectors.toList());
        FaceEntry.Entry faceEntry = FaceEntry.Entry
                .newBuilder()
                .addAllMatrix(data)
                .setName(name)
                .build();
        addEntryToDbCache(faceEntry);
        faceEntry
                .writeTo(new FileOutputStream(Paths
                        .get(DB_BASE, UUID.randomUUID() + ".pb")
                        .toFile()));
        redirectAttributes.addFlashAttribute("message",
                "successfully!");
        return "redirect:/register/index";
    }

    @RequestMapping("/identify")
    public String identify(@RequestParam("face") MultipartFile face,
                           RedirectAttributes redirectAttributes) throws IOException {
        FaceMatrix.Matrix matrix = getMatrix(face.getBytes());
        List<FaceEntry.Entry> names = dbCache
                .values()
                .stream()
                .flatMap(List::stream)
                .filter(t -> {
                    double compute = new EuclideanDistance().compute(
                            unwrap(t), unwrap(matrix));
                    return compute < 1.0;
                })
                .collect(Collectors.toList());
        redirectAttributes.addFlashAttribute("matches", names
                .stream()
                .map(FaceEntry.Entry::getName)
                .collect(Collectors.joining(",")));
        return "redirect:/register/index";
    }

    private FaceMatrix.Matrix getMatrix(byte[] file) {
        FaceMatrix.Face face = FaceMatrix.Face
                .newBuilder()
                .setFace(ByteString.copyFrom(file))
                .build();
        return client.getMatrix(face);
    }

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

    static void addEntryToDbCache(FaceEntry.Entry entry) {
        dbCache.compute(entry.getName(), (k, v) -> {
            List<FaceEntry.Entry> entries = Optional
                    .ofNullable(v)
                    .orElse(new ArrayList<>());
            entries.add(entry);
            return entries;
        });
    }
}
