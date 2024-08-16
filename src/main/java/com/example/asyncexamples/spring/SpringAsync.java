package com.example.asyncexamples.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class SpringAsync {
    private final RestTemplate restTemplate;

    public SpringAsync() {
        this.restTemplate = new RestTemplate();
    }

    public List<byte[]> downloadAsyncImages() throws ExecutionException, InterruptedException {
        List<String> urls = Arrays.asList(
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://get.wallhere.com/photo/landscape-mountains-lake-nature-reflection-grass-sky-river-national-park-valley-wilderness-Alps-tree-autumn-leaf-mountain-season-tarn-loch-mountainous-landforms-mountain-range-590185.jpg"
        );

        List<CompletableFuture<byte[]>> futures = new ArrayList<>();
        for (String url : urls) {
            futures.add(downloadAndProcessImageAsync(url));
        }
        List<byte[]> imageDataList = new ArrayList<>();
        for (CompletableFuture<byte[]> future : futures) {
            imageDataList.add(future.get());
        }
        return imageDataList;
    }

    @Async
    public CompletableFuture<byte[]> downloadAndProcessImageAsync(String url) {
        try {
            byte[] imageData = restTemplate.getForObject(url, byte[].class);
            return processImageAsync(imageData);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки " + url, e);
        }
    }

    @Async
    public CompletableFuture<byte[]> processImageAsync(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            int maxWidth = 100;
            int maxHeight = 100;
            int width = image.getWidth();
            int height = image.getHeight();
            double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
            int newWidth = (int) (width * ratio);
            int newHeight = (int) (height * ratio);

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpeg", bos);

            return CompletableFuture.completedFuture(bos.toByteArray());
        } catch (IOException e) {
            return CompletableFuture.completedFuture(new byte[0]);
        }//projectLoom?
    }
}
