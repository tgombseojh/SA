package com.yellowbus.project.place.search.controller;

import com.yellowbus.project.place.search.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@EnableAsync
class MemberControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext ctx;

    Member member;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                //.alwaysDo(print())
                .build();

        member = new Member();
        member.setEmail("aaa@gmail.com");
        member.setPassword("aaa");
        member.setName("seojh");
    }

    @Test
    @Order(1)
    public void test1() throws Exception {
        MvcResult mvcResult =
                mockMvc.perform(
                    post("/signup")
                        .param("email", member.getEmail())
                        .param("name", member.getName())
                        .param("password", member.getPassword())
                ).andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print()).andExpect(status().is(200));
    }

    @Test
    @Order(2)
    public void test2() throws Exception {
        MvcResult mvcResult =
                mockMvc.perform(
                        post("/signup")
                                .param("email", member.getEmail())
                                .param("name", member.getName())
                                .param("password", member.getPassword())
                ).andExpect(request().asyncStarted()).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult)).andDo(print()).andExpect(status().is(500));
    }

}