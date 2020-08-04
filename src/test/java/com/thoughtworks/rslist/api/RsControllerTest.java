package com.thoughtworks.rslist.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RsControllerTest {

    @Autowired
    MockMvc mockMvc;

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
}