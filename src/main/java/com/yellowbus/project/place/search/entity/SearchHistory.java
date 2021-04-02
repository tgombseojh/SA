package com.yellowbus.project.place.search.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Table(name = "SearchHistory")
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SearchHistory {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "userName")
    private String userName;

    @Column(name = "keyword")
    @NonNull
    private String keyWord;

    @Column(name = "date")
    @NonNull
    private Date date;

    public HashMap<String, Object> changeDateFormat(List<SearchHistory> searchHistoryList) {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        List<HashMap> resultList = new ArrayList<>();
        HashMap<String, String> hashMap;
        for(SearchHistory searchHistory : searchHistoryList) {
            hashMap = new HashMap<>();
            hashMap.put("date", simpleDateFormat.format(searchHistory.getDate()));
            hashMap.put("keyword", searchHistory.getKeyWord());

            resultList.add(hashMap);
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("history", resultList);

        return resultMap;
    }
}
