package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.*;

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
    isNull(newEvent, "requestBody is null");
    rsList.add(newEvent);
  }

  @PutMapping("rs/item/{id}")
  public void replaceOneById(@PathVariable int id, @RequestBody RsEvent rsEvent) {
    isNull(rsEvent, "requestBody is null");
    String newEventName = rsEvent.getEventName();
    String newKeyWord = rsEvent.getKeyWord();
    if (strIsBlank(newEventName)) {
      rsList.get(id - 1).setEventName(newEventName);
    }
    if (strIsBlank(newKeyWord)) {
      rsList.get(id - 1).setKeyWord(newKeyWord);
    }
  }

  @DeleteMapping("rs/item/{id}")
  public void deleteOneById(@PathVariable int id) {
    rsList.remove(rsList.get(id - 1));
  }

  public void isNull(Object object, String msg) {
    if (object == null) {
      throw new RuntimeException(msg);
    }
  }

  public boolean strIsBlank(String str) {
    if (str == null) return false;
    return !str.isEmpty();
  }

}
