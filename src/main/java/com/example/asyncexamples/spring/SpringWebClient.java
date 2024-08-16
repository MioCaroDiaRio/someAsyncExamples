package com.example.asyncexamples.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SpringWebClient {
    private final WebClient webClient;

    public SpringWebClient(WebClient.Builder builder) {
        this.webClient = builder.exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    public Mono<List<byte[]>> downloadAsyncImages() {
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
        return Flux.fromIterable(urls)
                .flatMap(this::downloadAndProcessImageAsync)
                .collectList();
    }
//stepVeryfier
    //testWebClient
    public Mono<byte[]> downloadAndProcessImageAsync(String url) {
        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(this::processImageAsync);
    }

    public Mono<byte[]> processImageAsync(byte[] imageData) {
        return Mono.fromCallable(() -> {
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

                return bos.toByteArray();
            } catch (IOException e) {
                log.error("Error processing image: {}", e.getMessage());
                return new byte[0];
            }
        });
    }
}