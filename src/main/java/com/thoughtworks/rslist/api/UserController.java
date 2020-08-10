package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.service.UserService;

import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import com.thoughtworks.rslist.validation.ValidationGroup;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity registerUser(@RequestBody @Validated(ValidationGroup.class) UserDto userDto) {
        userService.createAUser(userDto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Integer id) throws OutOfIndexException, ContentEmptyException {
        return userService.getUserById(id);
    }

    @DeleteMapping("user/{id}")
    public void deleteOneById(@PathVariable Integer id) throws OutOfIndexException {
        userService.deleteUserById(id);
    }

    @ExceptionHandler({OutOfIndexException.class, MethodArgumentNotValidException.class, ContentEmptyException.class})
    public ResponseEntity handleException(Exception ex) {
        Integer condition = GlobalExceptionHandler.OTHER_EXCEPTION;
        if (ex instanceof MethodArgumentNotValidException) condition =GlobalExceptionHandler.INVAILD_FOR_USER;

        log.error("Method {} error {}",this.getClass().getName(), ex.getClass());

        return GlobalExceptionHandler.globalExHandle(ex, condition);
    }

}
