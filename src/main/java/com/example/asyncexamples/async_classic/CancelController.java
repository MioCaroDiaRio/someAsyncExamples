package com.example.asyncexamples.async_classic;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CancelController {

    private final CompletableFutureExampleService completableFutureExampleService;

    public CancelController(CompletableFutureExampleService completableFutureExampleService) {
        this.completableFutureExampleService = completableFutureExampleService;
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelTask(@RequestParam("url") String taskId) {
        completableFutureExampleService.cancelTask(taskId);
        return ResponseEntity.ok("Task cancelled");
    }
}