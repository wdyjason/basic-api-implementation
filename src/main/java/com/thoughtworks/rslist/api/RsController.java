package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.validation.PatchForRsEventValidation;
import com.thoughtworks.rslist.validation.ValidationGroup;
import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thoughtworks.rslist.api.UserController.userList;
import static com.thoughtworks.rslist.utils.Utils.*;

@Slf4j
@RestController
public class RsController {
  private  User oldUser =
          new User("oldUser", 20, "male", "a@qq.com", "18888888888");

  public  List<RsEvent> rsList = Stream.of(new RsEvent("第一条事件", "经济", oldUser),
          new RsEvent("第二条事件", "文化", oldUser),
          new RsEvent("第三条事件", "政治", oldUser))
          .collect(Collectors.toList());

  @Autowired
  private RsEventRepository rsEventRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("rs/list")
  public ResponseEntity getAllInList(@RequestParam(required = false) Integer startIndex,
                                     @RequestParam(required = false) Integer endIndex) throws OutOfIndexException {
    if (startIndex == null || endIndex == null) return ResponseEntity.status(HttpStatus.OK).body(rsList);
    if (startIndex < 0 || endIndex > rsList.size()) {
      throw new OutOfIndexException("invalid request param");
    }
    return ResponseEntity.status(HttpStatus.OK).body(rsList.subList(startIndex - 1, endIndex));
  }

  @PostMapping("rs/item")
  public ResponseEntity addOne(@RequestBody @Valid RsEventEntity newEvent) throws ContentEmptyException {
    isNull(newEvent, "requestBody is null");
    if (userRepository.existsById(newEvent.getUserId())) {
      rsEventRepository.save(newEvent);
    } else {
      throw new ContentEmptyException("invalid user to add rsEvent");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body("index: " + (rsList.size() - 1));
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
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @DeleteMapping("rs/item/{id}")
  public ResponseEntity deleteOneById(@PathVariable int id) {
    rsList.remove(rsList.get(id - 1));
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @GetMapping("rs/{id}")
  public ResponseEntity getOneById(@PathVariable int id) throws OutOfIndexException {
    if (id < 0 || id >= rsList.size()) {
      throw new OutOfIndexException("invalid index");
    }
    return ResponseEntity.status(HttpStatus.OK).body(rsList.get(id - 1));
  }

  @Transactional
  @PatchMapping("rs/{rsEventId}")
  public ResponseEntity patchOneEvent(@PathVariable(name = "rsEventId") @NotNull int eventId,
                            @RequestBody @Validated({PatchForRsEventValidation.class}) RsEventEntity rsEvent) {
    Optional<RsEventEntity> rsEventWarp = rsEventRepository.findById(eventId);
    if (rsEventWarp.isPresent() && rsEventWarp.get().getUserId() == rsEvent.getUserId()) {

      if (rsEvent.getEventName() != null)
        rsEventRepository.updateEventNameById(eventId, rsEvent.getEventName());

      if (rsEvent.getKeyWord() != null)
        rsEventRepository.updateKeyWordById(eventId, rsEvent.getKeyWord());

      return ResponseEntity.status(HttpStatus.OK).build();
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @ExceptionHandler({OutOfIndexException.class, MethodArgumentNotValidException.class, ContentEmptyException.class})
  public ResponseEntity handleException(Exception ex) {
    Integer condition = GlobalExceptionHandler.OTHER_EXCEPTION;
    if (ex instanceof MethodArgumentNotValidException) condition =GlobalExceptionHandler.INVAILD_FOR_RSEVENT;

    log.error("Method {} error {}",this.getClass().getName(), ex.getClass());

    return GlobalExceptionHandler.globalExHandle(ex, condition);
  }
}
