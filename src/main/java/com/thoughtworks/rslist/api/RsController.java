package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.DTO.RsEventDTO;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static com.thoughtworks.rslist.utils.Utils.*;

@Slf4j
@RestController
public class RsController {

  @Autowired
  private RsEventRepository rsEventRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("rs/list")
  public ResponseEntity getAllInList(@RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) Integer pageIndex) {
    List<RsEventDTO> resultList;

    if (pageSize != null&& pageIndex != null) {
      Pageable page = PageRequest.of(pageIndex - 1, pageSize);
      resultList = rsEventRepository.findAll(page)
              .stream()
              .map(f -> RsEventDTO.fromEntity(f))
              .collect(Collectors.toList());
      return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }
    resultList = rsEventRepository.findAll()
            .stream()
            .map(f -> RsEventDTO.fromEntity(f))
            .collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(resultList);
  }

  @GetMapping("rs/{id}")
  public ResponseEntity getOneById(@PathVariable int id) throws OutOfIndexException {
    if (!rsEventRepository.existsById(id)) {
      throw new OutOfIndexException("invalid id");
    }
    RsEventEntity result = rsEventRepository.findById(id).get();
    return ResponseEntity.status(HttpStatus.OK).body(RsEventDTO.fromEntity(result));
  }

  @PostMapping("rs/item")
  public ResponseEntity addOne(@RequestBody @Valid RsEventEntity newEvent) throws ContentEmptyException {
    isNull(newEvent, "requestBody is null");
    if (userRepository.existsById(newEvent.getUserId())) {
      rsEventRepository.save(newEvent);
    } else {
      throw new ContentEmptyException("invalid user to add rsEvent");
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("rs/item")
  @Transactional
  public ResponseEntity replaceOneById(@RequestBody @Valid RsEventDTO rsEventDTO) {
    isNull(rsEventDTO, "requestBody is null");

    if (!userRepository.existsById(rsEventDTO.getUserId())) {
      throw new RuntimeException("this user has not been registered");
    }

    rsEventRepository.updateRsEventEntityById(rsEventDTO.toEntity());
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("rs/item/{id}")
  public ResponseEntity deleteOneById(@PathVariable int id) {
    rsEventRepository.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Transactional
  @PatchMapping("rs/{rsEventId}")
  public ResponseEntity patchOneEvent(@PathVariable @NotNull int rsEventId,
                                      @RequestParam String eventName,
                                      @RequestParam String keyWord) {
    if (rsEventRepository.existsById(rsEventId)) {

      if (strIsBlank(eventName)) {
        rsEventRepository.updateEventNameById(rsEventId, eventName);
      }

      if (strIsBlank(keyWord)) {
        rsEventRepository.updateKeyWordById(rsEventId, keyWord);
      }

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
