package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.validation.ValidationGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    public static List<User> userList = new ArrayList<>();

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity registerUser(@RequestBody @Validated(ValidationGroup.class) User user) {
        userList.add(user);
        userRepository.save(user.toEntity());
        return new ResponseEntity("index: " + (userList.size() - 1), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userList;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleException(MethodArgumentNotValidException ex) {
        log.error("Method {} error",this.getClass().getName());
        return GlobalExceptionHandler.globalExHandle(ex, GlobalExceptionHandler.INVAILD_FOR_USER);
    }

}
