package com.example.asyncexamples.async_classic;

import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class CompletableFutureExampleService {
    private final RestTemplate restTemplate;
    private final ExecutorService executor;
    public final Map<String, CompletableFuture<byte[]>> taskFutures = new ConcurrentHashMap<>();

    public CompletableFutureExampleService(ExecutorService executor) {
        this.restTemplate = new RestTemplate();
        this.executor = executor;
    }

    public List<byte[]> downloadAsyncImages() {
        List<String> urls = Arrays.asList(
                "https://s1.1zoom.ru/big3/399/339975-svetik.jpg",
                "https://get.wallhere.com/photo/landscape-mountains-lake-nature-reflection-grass-sky-river-national-park-valley-wilderness-Alps-tree-autumn-leaf-mountain-season-tarn-loch-mountainous-landforms-mountain-range-590185.jpg"
        );
        try {
            int i = 0;
            List<CompletableFuture<byte[]>> futures = new ArrayList<>();
            for (String url : urls) {
                CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(() -> {
                            try {
                                return restTemplate.getForObject(url, byte[].class);
                            } catch (Exception e) {
                                throw new RuntimeException("Ошибка загрузки " + url, e);
                            }
                        }, executor)
                        .thenApplyAsync(this::processImage, executor)
                        .orTimeout(10, TimeUnit.SECONDS) //возвращаем нал для конкретного изображения,
                        // если картинка обрабатывается дольше 10 секунд
                        .exceptionally(e -> {
                            if (e instanceof CancellationException) {
                                log.info("Task was cancelled");
                                return null;
                            } else {
                                log.error("Ошибка сжатия", e);
                                return null;
                            }
                        });

                futures.add(future);
                //добавляем логику отмены, например для конкретной картинки
                taskFutures.put(Integer.toString(i), future);
                i++;
            }

            List<byte[]> results = new ArrayList<>();
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            //Добавляем таймаут, если обработка картинок займет слишком много времени - выкидываем ошибку
            try {
                allFutures.get(30, TimeUnit.SECONDS); // Ждем либо завершения, либо 30 секунд
            } catch (TimeoutException e) {
                log.info("Timeout exceeded");
            } catch (CancellationException e) {
                log.info("Task was cancelled");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted", e);
            } catch (ExecutionException e) {
                log.error("Execution error", e);
            }

            for (CompletableFuture<byte[]> future : futures) {
                if (future.isCancelled()) {
                    log.info("Task was cancelled");
                    continue;
                }

                byte[] result = future.getNow(null); //Можем возвращать нал в случае отсутствия
                //значения, например, при ошибке. Но тут не актуально, поскольку метод сжатия при ошибке
                //вернет пустой массив
                if (result != null) {
                    results.add(result);
                }
            }
            return results;
        } finally {

        }
    }

    public void cancelTask(String taskId) {
        CompletableFuture<byte[]> future = taskFutures.get(taskId);
        if (future != null) {
            future.cancel(true);
            taskFutures.remove(taskId);
        }
    }

    private byte[] processImage(byte[] imageData) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Thread.sleep(50000);
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
            ImageIO.write(resizedImage, "jpeg", bos);

            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}