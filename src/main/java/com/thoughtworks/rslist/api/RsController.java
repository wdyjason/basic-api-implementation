package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.validation.ValidationGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thoughtworks.rslist.api.UserController.userList;
import static com.thoughtworks.rslist.utils.Utils.isNull;
import static com.thoughtworks.rslist.utils.Utils.strIsBlank;

@RestController
public class RsController {
  private  User oldUser =
          new User("oldUser", 20, "male", "a@qq.com", "18888888888");

  private  List<RsEvent> rsList = Stream.of(new RsEvent("第一条事件", "经济", oldUser),
          new RsEvent("第二条事件", "文化", oldUser),
          new RsEvent("第三条事件", "政治", oldUser))
          .collect(Collectors.toList());

  @GetMapping("rs/list")
  public ResponseEntity getAllInList(@RequestParam(required = false) Integer startIndex,
                                     @RequestParam(required = false) Integer endIndex) {
    if (startIndex == null || endIndex == null) {
      return new ResponseEntity(rsList, HttpStatus.OK);
    }
    return new ResponseEntity(rsList.subList(startIndex - 1, endIndex), HttpStatus.OK);
  }

  @PostMapping("rs/item")
  public ResponseEntity addOne(@RequestBody @Validated(ValidationGroup.class) RsEvent newEvent) {
    isNull(newEvent, "requestBody is null");
    if (containSameUserInList(newEvent.getUser())) {
      rsList.add(newEvent);
    } else {
      userList.add(newEvent.getUser());
    }
    return new ResponseEntity("index: " + (rsList.size() - 1),HttpStatus.CREATED);
  }

  @PutMapping("rs/item/{id}")
  public ResponseEntity replaceOneById(@PathVariable int id, @RequestBody RsEvent rsEvent) {
    isNull(rsEvent, "requestBody is null");
    String newEventName = rsEvent.getEventName();
    String newKeyWord = rsEvent.getKeyWord();
    if (strIsBlank(newEventName)) {
      rsList.get(id - 1).setEventName(newEventName);
    }
    if (strIsBlank(newKeyWord)) {
      rsList.get(id - 1).setKeyWord(newKeyWord);
    }
    return new ResponseEntity(HttpStatus.OK);
  }

  @DeleteMapping("rs/item/{id}")
  public ResponseEntity deleteOneById(@PathVariable int id) {
    rsList.remove(rsList.get(id - 1));
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("rs/{id}")
  public ResponseEntity getOneById(@PathVariable int id) {
    return new ResponseEntity(rsList.get(id - 1), HttpStatus.OK);
  }

  public boolean containSameUserInList(User addUser) {
    for (User u : userList) {
      if (u.getUserName().equals(addUser.getUserName()))
        return true;
    }
    return false;
  }
}
