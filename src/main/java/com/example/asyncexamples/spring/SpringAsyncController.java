package com.example.asyncexamples.spring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/spring")
public class SpringAsyncController {

    private final SpringAsync imageService;
    private final SpringAsyncAnnotationCorrect correctImageService;
    private final SpringWebClient springWebClient;

    public SpringAsyncController(SpringAsync imageService, SpringAsyncAnnotationCorrect correctImageService, SpringWebClient springWebClient) {
        this.imageService = imageService;
        this.correctImageService = correctImageService;
        this.springWebClient = springWebClient;
    }

    @GetMapping("/asyncImages")
    public ResponseEntity<List<byte[]>> downloadAsyncImages() throws ExecutionException, InterruptedException {
        List<byte[]> imageDataList = imageService.downloadAsyncImages();
        return new ResponseEntity<>(imageDataList, HttpStatus.OK);
    }
    @GetMapping("/correctAsyncImages")
    public ResponseEntity<List<byte[]>> downloadCorrectAsyncImages() throws ExecutionException, InterruptedException, TimeoutException {
        List<byte[]> imageDataList = correctImageService.downloadAsyncImages();
        return new ResponseEntity<>(imageDataList, HttpStatus.OK);
    }
    /*Важно: Тут есть минимум 4 разных подхода, зависит от того, кто наш потребитель.
    Можно сделать так, как сделал я - в целом допустимый вариант, особенно если надо общаться с фронтом
    однако если нашим потребителем является другой микросервис, поддерживающий асинхронность и\или
    реактивность, то мы можем либо возвращать объект Mono напрямую и давать ему разбираться самому:
    public Mono<List<byte[]>> downloadWebClientImages() {
        return springWebClient.downloadAsyncImages();
    }
    Либо же использовать блокирующий вывод и делать что-то вроде:
    public List<byte[]> downloadWebClientImages() throws ExecutionException, InterruptedException {
        return springWebClient.downloadAsyncImages().toFuture().get();
    }

    По сути, любой вариант имеет смылс. Главное - использовать подходящие запросы уже на фронте.
    В случае с JSом это бы выглядело примерно так:
    fetch('http://localhost:8080/spring/webClientImages')
        .then(response => response.json())
        .then(images => {
            console.log(images);
        })
        .catch(error => {
            console.error(error);
        });
    Либо же подойдет любой другой вариант асинхронного запроса.
    В случае с микрами нас бы вполне устроил запрос через WebClient, например, так (при условии, что
    в webClient сконфигурирован baseURL):
        public Mono<List<byte[]>> downloadAsyncImages() {
        return webClient.get().uri("/webClientImages")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<byte[]>>() {});
    }
    */
    @CrossOrigin
    @GetMapping("/webClientImages")
    public CompletableFuture<List<byte[]>> downloadWebClientImages() {
        return springWebClient.downloadAsyncImages().toFuture();
    }
}

