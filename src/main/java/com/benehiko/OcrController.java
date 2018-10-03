package com.benehiko;

import com.OcrHelper;
import com.company.acs.AcsApplication;
import com.company.acs.acs.acs.device.Device;
import com.company.acs.acs.acs.device.DeviceManager;
import com.company.acs.acs.acs.image.Image;
import com.company.acs.acs.acs.image.ImageImpl;
import com.company.acs.acs.acs.image.ImageManager;
import com.company.acs.acs.acs.numberplate.Numberplate;
import com.company.acs.acs.acs.numberplate.NumberplateImpl;
import com.company.acs.acs.acs.numberplate.NumberplateManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/ocr")
@EnableConfigurationProperties(MultipartConfigurations.class)
public class OcrController {

    private final DeviceManager deviceManager;
    private final ImageManager imageManager;
    private final NumberplateManager numberplateManager;

    public OcrController(AcsApplication app) {
        deviceManager = app.getOrThrow(DeviceManager.class);
        imageManager = app.getOrThrow(ImageManager.class);
        numberplateManager = app.getOrThrow(NumberplateManager.class);
    }

    @PostMapping("/pic/pi")
    @ResponseBody
    void getPiOcr(@RequestParam("timestamp") String timestamp, @RequestParam("mac") String mac, @RequestParam("images") List<MultipartFile> images) {
        List<byte[]> imgs = getBytes(images);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                backdrop(imgs, mac, timestamp);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @PostMapping("/pic/mobile")
    @ResponseBody
    List<String> getMobileOcr(@RequestParam("timestamp") String timestamp, @RequestParam("mac") String mac, @RequestParam("images") List<MultipartFile> images) throws ExecutionException, InterruptedException {
        List<byte[]> imgs = getBytes(images);
        return backdrop(imgs, mac, timestamp);
    }

    private CompletableFuture<OcrHelper> doOCR(byte[] bytes) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OcrHelper ocr = new OcrHelper(bytes);
                ocr.run();
                return ocr;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return null;
        });

    }

    private List<String> backdrop(List<byte[]> b, String mac, String timestamp) throws ExecutionException, InterruptedException {
        List<CompletableFuture<OcrHelper>> ocrResults = b.stream().map(this::doOCR).collect(toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                ocrResults.toArray(new CompletableFuture[ocrResults.size()])
        );

        CompletableFuture<List<OcrHelper>> ocrFuture = allFutures.thenApply(v -> ocrResults.stream()
                .map(pageContentFuture -> pageContentFuture.join())
                .collect(toList()));

        List<OcrHelper> results = ocrFuture.get();
        List<String> outputs = new ArrayList<>();

        results.forEach(ocr -> {
            if (ocr != null) {
                outputs.add(ocr.getResult());
                Device device = deviceManager.stream().filter(Device.MAC.containsIgnoreCase(mac)).findFirst().orElse(null);
                if (device != null) {
                    Blob blob = null;
                    try {
                        blob = new javax.sql.rowset.serial.SerialBlob(ocr.getBytes());
                        Image image = new ImageImpl().setImage(blob).setImageDevice(device.getDeviceId()).setTimeStamp(Timestamp.valueOf(timestamp));
                        imageManager.persist(image);
                        Numberplate numberplate = new NumberplateImpl().setNumberplateImage(image.getImageId()).setNumberplatestring(ocr.getResult());
                        numberplateManager.persist(numberplate);
                    } catch (SQLException | SpeedmentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return outputs;
    }

    private List<byte[]> getBytes(List<MultipartFile> images){
        List<byte[]> imgs = new ArrayList<>();
        images.forEach((image) -> {
            try {
                byte[] b = image.getBytes();
                imgs.add(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return imgs;
    }
}
