package com.yellowbus.project.place.search.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yellowbus.project.place.search.component.PlaceComponent;
import com.yellowbus.project.place.search.entity.HotKeyWord;
import com.yellowbus.project.place.search.entity.Member;
import com.yellowbus.project.place.search.entity.SearchHistory;
import com.yellowbus.project.place.search.entity.SearchResult;
import com.yellowbus.project.place.search.exception.KakaoAPIException;
import com.yellowbus.project.place.search.exception.NaverAPIException;
import com.yellowbus.project.place.search.repository.HotKeyWordRepository;
import com.yellowbus.project.place.search.repository.SearchHistoryRepository;
import com.yellowbus.project.place.search.repository.SearchResultRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
@Slf4j
public class PlaceService {

    PlaceComponent placeComponent;
    HotKeyWordRepository hotKeyWordRepository;
    SearchHistoryRepository searchHistoryRepository;
    SearchResultRepository searchResultRepository;

    Gson gson;

    public HashMap<String, Object> v3Place(String searchWord, Member userInfo) throws Exception {
        log.debug(" ========= PlaceService v3Place ========= ");
        log.debug("  "+Thread.currentThread().getThreadGroup().getName());
        log.debug("  "+Thread.currentThread().getName());

        // Async Job1 // 누가 언제 무엇을 검색했는지를 기록
        placeComponent.saveSearchHistory(searchWord, userInfo);

        // Async Job2 // 인기검색어 10개를 제공하기 위해서 검색할때마다 카운트 증가
        placeComponent.saveHotKeyWord(searchWord);

        // redis cache 를 도입하자.. 장소는 실시간으로 바뀌는 성격의 데이터가 아니므로, 캐시 유효시간은 1시간으로 설정하자..
        // 검색어가 cache 에 있으면 API를 호출하지 않는다..
        // 없으면, 호출하고 나서 그 결과를 cache 에 넣는다.
        Optional<SearchResult> cache = placeComponent.findToCache(searchWord);
        HashMap<String, Object> kakaoNaverPlace;
        if (cache.isEmpty()) {
            log.info(" === cache empty === ");
            // Async Job3
            CompletableFuture<List<String>> task1 = placeComponent.kakaoPlaceAPI(searchWord);
            try {
                task1.join();
            } catch (Exception e) {
                throw new KakaoAPIException(e.getMessage());
            }

            // Async Job4
            CompletableFuture<List<String>> task2 = placeComponent.naverPlaceAPI(searchWord);
            try {
                task2.join();
            } catch (Exception e) {
                throw new NaverAPIException(e.getMessage());
            }

            // Async Job3 & 4 가 완료되면 정렬 및 합치기
            kakaoNaverPlace = task1.thenCombine(task2, (kakao, naver) -> placeComponent.combineKakaoAndNaver(kakao, naver)).get();

            // caching
            placeComponent.saveSearchResult(searchWord, gson.toJson(kakaoNaverPlace));
        } else {
            Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
            kakaoNaverPlace = gson.fromJson(cache.get().getResult(), type);
            log.debug("result from cache : "+kakaoNaverPlace);
        }

        // todo 결과값이 캐시에 있어서 빠르게 리턴될 경우, job1,2 가 완료되지 않았다면 부모쓰레드가 죽기때문에 job1,2 가 실행되지 않을 가능성이 있지 않을까.
        return kakaoNaverPlace;
    }

    public HashMap<String, Object> getSearchHistory(Member userInfo) {
        log.debug(" ========= PlaceService getSearchHistory ========= ");
        log.debug("  "+Thread.currentThread().getThreadGroup().getName());
        log.debug("  "+Thread.currentThread().getName());

        List<SearchHistory> searchHistoryList = searchHistoryRepository.findTop20ByUserIdOrderByDateDesc(userInfo.getUserId());
        log.debug(" ========= searchHistoryList : "+searchHistoryList);

        return new SearchHistory().changeDateFormat(searchHistoryList);
    }

    public HashMap<String, Object> getHotKeyWord() {
        List<HotKeyWord> hotKeyWordList = hotKeyWordRepository.findTop10ByOrderBySearchCountDesc();

        List<HashMap> resultList = new ArrayList<>();
        HashMap<String, Object> hashMap;
        for(HotKeyWord hotKeyWord : hotKeyWordList) {
            hashMap = new HashMap<>();
            hashMap.put("keyword", hotKeyWord.getKeyWord());
            hashMap.put("search_count", hotKeyWord.getSearchCount());

            resultList.add(hashMap);
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("hot10keywords", resultList);

        return resultMap;
    }


}
