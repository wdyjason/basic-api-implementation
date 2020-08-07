package com.thoughtworks.rslist.api;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.DTO.RsEventDTO;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.entity.VoteEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private VoteRepository voteRepository;

    @Autowired
    ApplicationContext applicationContext;

    private int savedUserId;
    private int savedVoteId;
    private int savedEventId;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private LocalDateTime testTime = LocalDateTime.now();
    private User oldUser =
            new User("oldUser", 20, "male", "a@qq.com", "18888888888");


    private UserEntity initialUserEntity = UserEntity.builder()
            .age(20)
            .userName("oldUser")
            .gender("male")
            .email("a@qq.com")
            .phone("18888888888")
            .tickets(9)
            .build();

    private RsEventEntity initialRsEventEntity = RsEventEntity.builder()
            .eventName("eName")
            .keyWord("eKeyWord")
            .build();

    private VoteEntity initialVoteEntity = VoteEntity.builder()
            .voteNum(1)
            .voteTime(testTime)
            .build();

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        voteRepository.deleteAll();

        savedUserId = userRepository.save(initialUserEntity).getId();
        initialRsEventEntity.setUserId(savedUserId);
        initialVoteEntity.setUserId(savedUserId);

        savedEventId = rsEventRepository.save(initialRsEventEntity).getId();
        initialVoteEntity.setRsEventEntityId(savedEventId);

        savedVoteId = voteRepository.save(initialVoteEntity).getId();
    }

    @Test
    public void should_get_rs_list_when_request_get_for_all() throws Exception {
        RsEventEntity list2ndItem = RsEventEntity.builder().eventName("2nd").keyWord("word").userId(savedUserId).build();
        RsEventEntity list3rdItem = RsEventEntity.builder().eventName("3rd").keyWord("word").userId(savedUserId).build();
        rsEventRepository.saveAll(Arrays.asList(list2ndItem, list3rdItem));

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("eName"))
                .andExpect(jsonPath("$[0].keyWord").value("eKeyWord"))
                .andExpect(jsonPath("$[0].voteNum").value(1))
                .andExpect(jsonPath("$[1].eventName").value("2nd"))
                .andExpect(jsonPath("$[1].keyWord").value("word"))
                .andExpect(jsonPath("$[1].voteNum").value(0))
                .andExpect(jsonPath("$[2].eventName").value("3rd"))
                .andExpect(jsonPath("$[2].keyWord").value("word"))
                .andExpect(jsonPath("$[2].voteNum").value(0))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_page_when_request_with_parameters() throws Exception {
        RsEventEntity list2ndItem = RsEventEntity.builder().eventName("2nd").keyWord("word").userId(savedUserId).build();
        RsEventEntity list3rdItem = RsEventEntity.builder().eventName("3rd").keyWord("word").userId(savedUserId).build();
        rsEventRepository.saveAll(Arrays.asList(list2ndItem, list3rdItem));

        mockMvc.perform(get("/rs/list?pageIndex=2&pageSize=2"))
                .andExpect(jsonPath("$[0].eventName").value("3rd"))
                .andExpect(jsonPath("$[0].keyWord").value("word"))
                .andExpect(jsonPath("$[0].voteNum").value(0))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_item_successful_when_receive_old_user_post_request() throws Exception {
        String postEventStr = "{\"eventName\":\"第四条事件\",\"keyWord\":\"军事\"," +
                "\"userId\": "+ savedUserId +"}";
        mockMvc.perform(post("/rs/item")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postEventStr))
                .andExpect(status().isCreated());

        assertEquals(2, rsEventRepository.count());
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
        String putEventStr ="{\"id\":" + savedEventId +
                ",\"eventName\":\"changed\",\"keyWord\":\"changed\",\"voteNum\":null,\"userId\":" + savedUserId + "}";
        mockMvc.perform(put("/rs/item/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(putEventStr))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/" + savedEventId))
                .andExpect(jsonPath("$.eventName").value("changed"))
                .andExpect(jsonPath("$.keyWord").value("changed"))
                .andExpect(jsonPath("$.voteNum").value(1))
                .andExpect(status().isOk());
    }

    @Test
    public void should_delete_one_by_id_successful() throws Exception {
        mockMvc.perform(delete("/rs/item/" + savedEventId))
                .andExpect(status().isOk());
        assertEquals(false, rsEventRepository.existsById(savedEventId));
    }

    @Test
    public void should_get_one_by_id_successful() throws Exception {

        mockMvc.perform(get("/rs/" + savedEventId))
                .andExpect(jsonPath("$.eventName").value("eName"))
                .andExpect(jsonPath("$.keyWord").value("eKeyWord"))
                .andExpect(jsonPath("$.voteNum").value(1))
                .andExpect(jsonPath("$.id").value(savedEventId))
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

//    @Test
//    public void user_name_max_size_is_8_when_post_event() throws Exception {
//        String oldUserNameMoreThan8 = "{\"eventName\":\"修改的事件\",\"keyWord\":\"未分类\"," +
//                "\"user\":{\"userName\":\"oldUserHhh\",\"age\":20,\"gender\":\"male\",\"email\":\"a@qq.com\",\"phone\":\"18888888888\"}}";
//        mockMvc.perform(post("/rs/item").content(oldUserNameMoreThan8).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

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

//    @Test
//    public void should_throw_exception_when_index_pout_of_bounds() throws Exception {
//        mockMvc.perform(get("/rs/list/?startIndex=1&endIndex=5"))
//                .andExpect(content().string("{\"error\":\"invalid request param\"}"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    public void should_throw_invalid_id_when_index_pout_of_bounds() throws Exception {
        mockMvc.perform(get("/rs/0"))
                .andExpect(content().string("{\"error\":\"invalid id\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_throw_method_argument_not_valid_exception_when_post_for_invalid_RsEvent() throws Exception {

        String eventStr = "{\"eventName\":null,\"keyWord\":\"未分类\"," +
                "\"user\":1}";
        mockMvc.perform(post("/rs/item").content(eventStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"error\":\"invalid param\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_update_rsEvent_successful_when_patch() throws Exception {
        int eventId =rsEventRepository.save(RsEventEntity.builder()
                .eventName("event")
                .keyWord("keyW")
                .build()).getId();

        mockMvc.perform(patch("/rs/" + eventId + "?eventName=&keyWord=未分类"))
                .andExpect(status().isOk());
        RsEventEntity rsEventEntity = rsEventRepository.findById(eventId).get();
        assertEquals("event", rsEventEntity.getEventName());
        assertEquals("未分类", rsEventEntity.getKeyWord());
    }
}