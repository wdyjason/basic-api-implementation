package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    LocalDateTime date = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        userRepository.save(UserEntity.builder()
                .age(20)
                .gender("male")
                .email("a@qq.com")
                .phone("18888888888")
                .userName("tUser")
                .tickets(10)
                .build());
    }

    @Test
    public void should_vote_successfully_if_vote_num_less_than_tickets() throws Exception {
        String voteStr = "{\"id\":1,\"voteNum\":1,\"userId\":1}";

        mockMvc.perform(post("/rs/vote/1").content(voteStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_vote_fail_if_vote_num_more_than_tickets() throws Exception {
        String voteStr = "{\"id\":1,\"voteNum\":11,\"userId\":1}";

        mockMvc.perform(post("/rs/vote/1").content(voteStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}