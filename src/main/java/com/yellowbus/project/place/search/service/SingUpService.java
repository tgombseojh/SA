package com.yellowbus.project.place.search.service;

import com.google.gson.Gson;
import com.yellowbus.project.place.search.entity.Member;
import com.yellowbus.project.place.search.exception.SignupException;
import com.yellowbus.project.place.search.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
@Slf4j
public class SingUpService {

    MemberRepository memberRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Gson gson;

    public HashMap<String, Object> signup(HashMap<String, Object> hashMap) {
        Member member = gson.fromJson(gson.toJsonTree(hashMap), Member.class);
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));

        try {
            member = memberRepository.save(member);
            if (member.getUserId()==null) throw new SignupException("sign up failed");
        } catch (DataIntegrityViolationException e) {
            throw new SignupException("User exist (다른 아이디를 사용해주세요)");
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("message", "You have successfully signed up");

        return resultMap;
    }

}
