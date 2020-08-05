package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private String oldUserStr =
            "{\"userName\":\"oldUser\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";

    @BeforeEach
    public void setUp() {
        UserController.userList.clear();
    }

    @Test
    public void should_add_user_successful() throws Exception {
        mockMvc.perform(post("/user").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(1, UserController.userList.size());
    }

    @Test
    public void user_name_max_size_is_8() throws Exception {
        String oldUserNameMoreThan8 =
                "{\"userName\":\"oldUserHhh\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/user").content(oldUserNameMoreThan8).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void  user_age_should_between_18_and_100() throws Exception {
        String oldUserAge101 =
                "{\"userName\":\"oldUser\",\"age\":101,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/user").content(oldUserAge101).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String oldUserAge17 =
                "{\"userName\":\"oldUser\",\"age\":17,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/user").content(oldUserAge17).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}