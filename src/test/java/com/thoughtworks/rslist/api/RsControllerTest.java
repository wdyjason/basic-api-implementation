package com.thoughtworks.rslist.api;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RsEventRepository rsEventRepository;

    @Autowired
    ApplicationContext applicationContext;

    private User oldUser =
            new User("oldUser", 20, "male", "a@qq.com", "18888888888");

    private String oldUserStr =
            "{\"userName\":\"oldUser\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";


    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        userRepository.save(UserEntity.builder()
                .id(1)
                .age(20)
                .userName("oldUser")
                .gender("male")
                .email("a@qq.com")
                .phone("18888888888")
                .build());

        RsController rsController = applicationContext.getBean(RsController.class);
        Field rsList = rsController.getClass().getField("rsList");
        rsList.set(rsController, Stream.of(new RsEvent("第一条事件", "经济", oldUser),
                new RsEvent("第二条事件", "文化", oldUser),
                new RsEvent("第三条事件", "政治", oldUser))
                .collect(Collectors.toList()));

        UserController.userList = Stream.of(oldUser).collect(Collectors.toList());
    }

    @Test
    public void should_get_rs_list_when_request_get_for_all() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("经济"))
                .andExpect(jsonPath("$[0]", not(hasKey("user"))))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("文化"))
                .andExpect(jsonPath("$[1]", not(hasKey("user"))))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyWord").value("政治"))
                .andExpect(jsonPath("$[2]", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_limit_rs_list_when_request_get_with_parameters() throws Exception {
        mockMvc.perform(get("/rs/list?startIndex=2&endIndex=3"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("文化"))
                .andExpect(jsonPath("$[0]", not(hasKey("user"))))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("政治"))
                .andExpect(jsonPath("$[1]", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_item_successful_when_receive_old_user_post_request() throws Exception {
        String postEventStr = "{\"eventName\":\"第四条事件\",\"keyWord\":\"军事\"," +
                "\"userId\": 1}";
        mockMvc.perform(post("/rs/item")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postEventStr))
                .andExpect(status().isCreated());

        List<RsEventEntity> rsEventEntities = rsEventRepository.findAll();
        assertEquals(1, rsEventEntities.size());
    }

    @Test
    public void should_bad_request_when_receive_new_user_post_request() throws Exception {
        String postEventStr = "{\"eventName\":\"第四条事件\",\"keyWord\":\"军事\"," +
                "\"userId\": 2}";
        mockMvc.perform(post("/rs/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postEventStr))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_replace_one_by_id_successful() throws Exception {
        RsEvent putEvent = new RsEvent("修改的事件", "未分类", oldUser);
        ObjectMapper objectMapper = new ObjectMapper();
        String putEventStr = objectMapper.writeValueAsString(putEvent);
        mockMvc.perform(put("/rs/item/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(putEventStr))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("修改的事件"))
                .andExpect(jsonPath("$[0].keyWord").value("未分类"))
                .andExpect(jsonPath("$[0]", not(hasKey("user"))))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("文化"))
                .andExpect(jsonPath("$[1]", not(hasKey("user"))))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyWord").value("政治"))
                .andExpect(jsonPath("$[2]", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

    @Test
    public void should_delete_one_by_id_successful() throws Exception {
        mockMvc.perform(delete("/rs/item/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("文化"))
                .andExpect(jsonPath("$[0]", not(hasKey("user"))))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("政治"))
                .andExpect(jsonPath("$[1]", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_one_by_id_successful() throws Exception {
        mockMvc.perform(get("/rs/2"))
                .andExpect(jsonPath("$.eventName").value("第二条事件"))
                .andExpect(jsonPath("$.keyWord").value("文化"))
                .andExpect(jsonPath("$", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

//    @Test
//    public void should_add_user_successful_when_post_new_user() throws Exception {
////        User newUser =
////                new User("newUser", 20, "male", "a@qq.com", "18888888888");
////        RsEvent postEvent = new RsEvent("修改的事件", "未分类", newUser);
////        ObjectMapper objectMapper = new ObjectMapper();
//        String eventStr = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
//                "\"user\":1}";
//        mockMvc.perform(post("/rs/item").content(eventStr).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().string("index: 2"))
//                .andExpect(status().isCreated());
////        assertEquals(newUser.toString(), UserController.userList.get(1).toString());
//    }

    @Test
    public void user_name_max_size_is_8_when_post_event() throws Exception {
        String oldUserNameMoreThan8 = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
                "\"user\":{\"userName\":\"oldUserHhh\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}}";
        mockMvc.perform(post("/rs/item").content(oldUserNameMoreThan8).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void  user_age_should_between_18_and_100_when_post_event() throws Exception {
        String oldUserAge101 = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
                "\"user\":{\"userName\":\"oldUser\",\"age\":101,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/rs/item").content(oldUserAge101).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String oldUserAge17 = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
                "\"user\":{\"userName\":\"oldUser\",\"age\":17,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/rs/item").content(oldUserAge17).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void user_gender_is_not_null_when_post_event() throws Exception {
        String oldUserGenderNull = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
                "\"user\":{\"userName\":\"oldUser\",\"age\":2 0,\"gender\":\"null\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/rs/item").content(oldUserGenderNull).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void user_email_should_correct_when_post_event() throws Exception {
        String oldUserEmailWrong = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
                "\"user\":{\"userName\":\"oldUser\",\"age\":2 0,\"gender\":\"male\",\"email\":\"aqq.com\",\"phone\":\"18888888888\"}";
        mockMvc.perform(post("/rs/item").content(oldUserEmailWrong).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void user_phone_should_correct_when_post_event() throws Exception {
        String oldUserPhoneWrong = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
                "\"user\":{\"userName\":\"oldUser\",\"age\":2 0,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888\"}";
        mockMvc.perform(post("/rs/item").content(oldUserPhoneWrong).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_throw_exception_when_index_pout_of_bounds() throws Exception {
        mockMvc.perform(get("/rs/list/?startIndex=1&endIndex=5"))
                .andExpect(content().string("{\"error\":\"invalid request param\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_throw_invalid_index_when_index_pout_of_bounds() throws Exception {
        mockMvc.perform(get("/rs/5"))
                .andExpect(content().string("{\"error\":\"invalid index\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_throw_MethodArgumentNotValidException_when_post_for_invalid_RsEvent() throws Exception {

        String eventStr = "{\"eventName\":null,\"keyWord\":\"未分类\"," +
                "\"user\":1}";
        mockMvc.perform(post("/rs/item").content(eventStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"error\":\"invalid param\"}"))
                .andExpect(status().isBadRequest());
    }

}