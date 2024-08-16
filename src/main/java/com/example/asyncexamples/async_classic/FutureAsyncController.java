package com.example.asyncexamples.async_classic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping
public class FutureAsyncController {
    private final FutureExampleService imageService;
    private final BasicLoader basicLoader;
    private final CompletableFutureExampleService completableFutureExampleService;

    public FutureAsyncController(FutureExampleService imageService, BasicLoader basicLoader, CompletableFutureExampleService completableFutureExampleService) {
        this.imageService = imageService;
        this.basicLoader = basicLoader;
        this.completableFutureExampleService = completableFutureExampleService;
    }

    @GetMapping("/asyncImages")
    public ResponseEntity<List<byte[]>> downloadAsyncImages() throws InterruptedException, ExecutionException {
        List<byte[]> imageDataList = imageService.downloadAsyncImages();
        return new ResponseEntity<>(imageDataList, HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/images")
    public ResponseEntity<List<byte[]>> downloadImages() {
        List<byte[]> imageDataList = basicLoader.downloadImages();
        return new ResponseEntity<>(imageDataList, HttpStatus.OK);
    }
    @GetMapping("/complitableFutureImages")
    public ResponseEntity<List<byte[]>> downloadComplitableAsyncImages() {
        List<byte[]> imageDataList = completableFutureExampleService.downloadAsyncImages();
        return new ResponseEntity<>(imageDataList, HttpStatus.OK);
    }


}
