package com.example.asyncexamples.async_classic;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
public class FutureExampleService {
    public List<byte[]> downloadAsyncImages() throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(10);

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
        RestTemplate restTemplate = new RestTemplate();

        List<Future<byte[]>> futures = new ArrayList<>();
        for (String url : urls) {

            Future<byte[]> future = executor.submit(() -> { //то же самое, что new Callable<byte[]>()
                System.out.println("Начали скачивать");
                try {
                    return restTemplate.getForObject(url, byte[].class);
                } catch (Exception e) {
                    System.err.println("ошибка загрузки: " + e.getMessage());
                    return null;
                } finally {
                    System.out.println("закончили скачивать");
                }
            });
            futures.add(future);
        }
        //Делаем какие-то подготовительные операции, например, синхронно обращаемся в какой-нибудь другой
        //микр для, условно, получения списка фильтров и дальнейшего применения их на картинку

        List<byte[]> imageDataList = new ArrayList<>();
        for (Future<byte[]> future : futures) {
            byte[] imageData = future.get(); // блокируемся до завершения скачивания
            if (imageData != null) {
                imageDataList.add(imageData);
            }
        }
        System.out.println("Скачивание успешно завершено");
        executor.shutdown();
        return imageDataList;
    }
}
