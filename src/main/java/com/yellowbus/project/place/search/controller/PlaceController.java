package com.yellowbus.project.place.search.controller;

import com.google.gson.Gson;
import com.yellowbus.project.place.search.entity.Member;
import com.yellowbus.project.place.search.service.PlaceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
@Slf4j
public class PlaceController {

    PlaceService placeService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    AsyncTaskExecutor taskExecutor;
    Gson gson;

    @GetMapping("/v1/place/{searchWord}")
    public CompletableFuture<HashMap<String, Object>> v3Place(@PathVariable String searchWord, Authentication authentication) {
        Member userInfo = (Member)authentication.getPrincipal();
        log.debug(" ========= PlaceController v3Place ========= ");
        log.debug("  "+Thread.currentThread().getThreadGroup().getName());
        log.debug("  "+Thread.currentThread().getName());
        log.debug("  "+userInfo.getUsername());

        return CompletableFuture.supplyAsync(() -> {
            try {
                return placeService.v3Place(searchWord, userInfo);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, taskExecutor);
    }

    @GetMapping("/v1/search/history")
    public CompletableFuture<HashMap<String, Object>> history(Authentication authentication) {
        Member userInfo = (Member)authentication.getPrincipal();
        log.debug(" ========= PlaceController history ========= ");
        log.debug("  "+Thread.currentThread().getThreadGroup().getName());
        log.debug("  "+Thread.currentThread().getName());
        log.debug("  "+userInfo.getUsername());

        return CompletableFuture.supplyAsync(() -> {
            try {
                return placeService.getSearchHistory(userInfo);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, taskExecutor);
    }

    @GetMapping("/v1/search/hot10keywords")
    public CompletableFuture<HashMap<String, Object>> hotKeyWord() {

        return CompletableFuture.supplyAsync(() -> {
            try {
                return placeService.getHotKeyWord();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, taskExecutor);
    }

}
