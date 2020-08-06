package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    private String oldUserStr =
            "{\"userName\":\"oldUser\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";

    @BeforeEach
    public void setUp() {
        UserController.userList.clear();
        userRepository.deleteAll();
    }

    @Test
    public void should_add_user_successful() throws Exception {
        mockMvc.perform(post("/user").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("index: 0"))
                .andExpect(status().isCreated());
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

    @Test
    public void user_gender_is_not_null() throws Exception {
        String oldUserGenderNull =
                "{\"userName\":\"oldUser\",\"age\":2 0,\"gender\":\"null\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/user").content(oldUserGenderNull).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void user_email_should_correct() throws Exception {
        String oldUserEmailWrong =
                "{\"userName\":\"oldUser\",\"age\":2 0,\"gender\":\"male\",\"email\":\"aqq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/user").content(oldUserEmailWrong).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void user_phone_should_correct() throws Exception {
        String oldUserPhoneWrong =
                "{\"userName\":\"oldUser\",\"age\":2 0,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888\"}";
        mockMvc.perform(post("/user").content(oldUserPhoneWrong).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_get_all_users() throws Exception {
        UserController.userList =
                Stream.of(new User("oldUser", 20, "male", "a@qq.com", "18888888888"))
                        .collect(Collectors.toList());
        mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$[0].user_name", is("oldUser")))
                .andExpect(jsonPath("$[0].user_age", is(20)))
                .andExpect(jsonPath("$[0].user_gender", is("male")))
                .andExpect(jsonPath("$[0].user_email", is("a@qq.com")))
                .andExpect(jsonPath("$[0].user_phone", is("18888888888")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_throw_MethodArgumentNotValidException_when_post_for_invalid_user() throws Exception {

        String invalidUserStr =
                "{\"userName\":\"oldUser\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888\"}";
        mockMvc.perform(post("/user").content(invalidUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"error\":\"invalid user\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_save_one_success() throws Exception {
        mockMvc.perform(post("/user").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("index: 0"))
                .andExpect(status().isCreated());
        List<UserEntity> userEntities = userRepository.findAll();
        assertEquals(1, userEntities.size());
    }

    @Test
    public void should_get_one_successful() throws Exception {
        mockMvc.perform(post("/user").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("index: 0"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/user/2").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_name", is("oldUser")))
                .andExpect(jsonPath("$.user_age",  is(20)))
                .andExpect(jsonPath("$.user_gender",  is("male")))
                .andExpect(jsonPath("$.user_email",  is("a@qq.com")))
                .andExpect(jsonPath("$.user_phone",  is("18888888888")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_delete_one_by_id_successful() throws Exception {
        mockMvc.perform(post("/user").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("index: 0"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/user/1").content(oldUserStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<UserEntity> userEntities = userRepository.findAll();
        assertEquals(0, userEntities.size());
    }

}