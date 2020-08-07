package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.DTO.RsEventDTO;
import com.thoughtworks.rslist.entity.RsEventEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import com.thoughtworks.rslist.service.RsService;
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

  @Autowired
  private RsService rsService;

  @GetMapping("rs/list")
  public List<RsEventDTO> getAllInList(@RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) Integer pageIndex) {
      return rsService.findAllRsEvent(pageIndex, pageSize);
  }

  @GetMapping("rs/{id}")
  public RsEventDTO getOneById(@PathVariable int id) throws OutOfIndexException {
    return rsService.getOneRsEventById(id);
  }

  @PostMapping("rs/item")
  public ResponseEntity addOne(@RequestBody @Valid RsEventEntity newEvent) throws ContentEmptyException {
    rsService.createRsEvent(newEvent);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("rs/item")
  public void replaceOneById(@RequestBody @Valid RsEventDTO rsEventDTO) {
    rsService.updateRsEvent(rsEventDTO);
  }

  @DeleteMapping("rs/item/{id}")
  public void deleteOneById(@PathVariable int id) {
    rsService.deleteRsEvent(id);
  }

  @Transactional
  @PatchMapping("rs/{rsEventId}")
  public ResponseEntity patchOneEvent(@PathVariable @NotNull int rsEventId,
                                      @RequestParam String eventName,
                                      @RequestParam String keyWord) {
    if (rsService.updatePartProperty(rsEventId, eventName, keyWord)) {
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
