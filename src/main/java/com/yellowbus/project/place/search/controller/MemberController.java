package com.yellowbus.project.place.search.controller;

import com.google.gson.Gson;
import com.yellowbus.project.place.search.entity.Member;
import com.yellowbus.project.place.search.exception.SignupException;
import com.yellowbus.project.place.search.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@AllArgsConstructor
@RestController
@Slf4j
public class MemberController {

    MemberService memberService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Gson gson;

    @PostMapping("/signup")
    public ResponseEntity<HashMap<String, Object>> signup(@RequestParam HashMap<String, Object> hashMap) {
        Member member = gson.fromJson(gson.toJsonTree(hashMap), Member.class);
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));

        log.debug("member : "+member);

        try {
            member = memberService.signup(member);
            if (member.getUserId()==null) throw new SignupException("sign up failed");
        } catch (DataIntegrityViolationException e) {
            throw new SignupException("User exist (다른 아이디를 사용해주세요)");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "You have successfully signed up");

        return ResponseEntity.ok(map);
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
