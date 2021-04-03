package com.yellowbus.project.place.search.controller;

import com.yellowbus.project.place.search.service.SingUpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
@Slf4j
public class MemberController {

    SingUpService singUpService;
    AsyncTaskExecutor taskExecutor;

    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<HashMap<String, Object>>> signup(@RequestParam HashMap<String, Object> hashMap) {

        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(singUpService.signup(hashMap), HttpStatus.OK),
            taskExecutor);
    }

    @PostMapping("/login/success")
    public ResponseEntity<HashMap<String, Object>> loginSuccess() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "Login succeed");

        return ResponseEntity.ok(map);
    }

    @PostMapping("/login/failure")
    public ResponseEntity<HashMap<String, Object>> fail() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "Login failure");

        return ResponseEntity.ok(map);
    }

}
