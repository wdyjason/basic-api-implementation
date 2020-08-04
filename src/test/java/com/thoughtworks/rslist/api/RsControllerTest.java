package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RsControllerTest {

    @Autowired
    public RsController rsController;

    private List<RsEvent> rsList = Stream.of(new RsEvent("第一条事件", "经济"),
            new RsEvent("第二条事件", "文化"), new RsEvent("第三条事件", "政治"))
            .collect(Collectors.toList());

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field rsListField = rsController.getClass().getDeclaredField("rsList");
        rsListField.setAccessible(true);
        rsListField.set(rsController, rsList);
        rsListField.setAccessible(false);
        mockMvc = MockMvcBuilders.standaloneSetup(rsController).build();
    }

    @Test
    public void should_get_rs_list_when_request_get_for_all() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("经济"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("文化"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyWord").value("政治"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_limit_rs_list_when_request_get_with_parameters() throws Exception {
        mockMvc.perform(get("/rs/list?startIndex=2&endIndex=3"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("文化"))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("政治"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_item_successful_when_receive_post_request() throws Exception {
        RsEvent postEvent = new RsEvent("第四条事件", "军事");
        ObjectMapper objectMapper = new ObjectMapper();
        String postEventStr = objectMapper.writeValueAsString(postEvent);
        mockMvc.perform(post("/rs/item")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postEventStr))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第一条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("经济"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("文化"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyWord").value("政治"))
                .andExpect(jsonPath("$[3].eventName").value("第四条事件"))
                .andExpect(jsonPath("$[3].keyWord").value("军事"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_replace_one_by_id_successful() throws Exception {
        RsEvent putEvent = new RsEvent("修改的事件", "未分类");
        ObjectMapper objectMapper = new ObjectMapper();
        String putEventStr = objectMapper.writeValueAsString(putEvent);
        mockMvc.perform(put("/rs/item/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(putEventStr))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("修改的事件"))
                .andExpect(jsonPath("$[0].keyWord").value("未分类"))
                .andExpect(jsonPath("$[1].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("文化"))
                .andExpect(jsonPath("$[2].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[2].keyWord").value("政治"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_delete_one_by_id_successful() throws Exception {
        mockMvc.perform(delete("/rs/item/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName").value("第二条事件"))
                .andExpect(jsonPath("$[0].keyWord").value("文化"))
                .andExpect(jsonPath("$[1].eventName").value("第三条事件"))
                .andExpect(jsonPath("$[1].keyWord").value("政治"))
                .andExpect(status().isOk());
    }
}