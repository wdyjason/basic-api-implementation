package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
  private List<RsEvent> rsList = Stream.of(new RsEvent("第一条事件", "经济"),
          new RsEvent("第二条事件", "文化"), new RsEvent("第三条事件", "政治"))
          .collect(Collectors.toList());

  @GetMapping("rs/list")
  public List<RsEvent> getAllInList(@RequestParam(required = false) Integer startIndex,
                                    @RequestParam(required = false) Integer endIndex) {
    if (startIndex == null || endIndex == null) {
      return rsList;
    }
    return rsList.subList(startIndex - 1, endIndex);
  }

  @PostMapping("rs/item")
  public void addOne(@RequestBody String newEventStr) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    RsEvent newEvent = objectMapper.readValue(newEventStr, RsEvent.class);
    if (newEvent == null) {
      throw new RuntimeException("RequestBody is illegal !");
    }
    rsList.add(newEvent);
  }

}
