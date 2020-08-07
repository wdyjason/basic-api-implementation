package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    LocalDateTime ldt = LocalDateTime.of(2020, 1, 6 ,10, 1, 0);

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
        String voteStr = "{\"id\":1,\"voteNum\":1,\"userId\":1,\"voteTime\":\"2019-10-31T20:30:59\"}";

        mockMvc.perform(post("/rs/vote/1").content(voteStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_vote_fail_if_vote_num_more_than_tickets() throws Exception {
        String voteStr = "{\"id\":1,\"voteNum\":11,\"userId\":1,\"voteTime\":\"2019-10-31T20:30:59\"}";

        mockMvc.perform(post("/rs/vote/1").content(voteStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_all_vote_during_a_time() throws Exception {
        VoteEntity vote1st = VoteEntity.builder()
                .voteNum(1)
                .voteTime(ldt)
                .userId(1)
                .build();
        LocalDateTime ldt2nd = ldt.plusDays(1);
        VoteEntity vote2nd = VoteEntity.builder()
                .voteNum(2)
                .voteTime(ldt2nd)
                .userId(2)
                .build();
        LocalDateTime ldt3rd = ldt.plusDays(1);
        VoteEntity vote3rd = VoteEntity.builder()
                .voteNum(3)
                .voteTime(ldt3rd)
                .userId(3)
                .build();
        voteRepository.saveAll(Arrays.asList(vote1st, vote2nd, vote3rd));

        mockMvc.perform(get("/rs/vote/history?startTime=2020-01-06T11:30:59&endTime=2020-01-07T20:30:59"))
                .andExpect(jsonPath("$[0].voteNum").value(2))
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(status().isOk());
    }


}