package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.exception.OutOfIndexException;
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
import java.util.Optional;

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

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Integer id) throws OutOfIndexException, ContentEmptyException {
        if (id == null) throw new OutOfIndexException("invalid id");

        Optional<UserEntity> userEntityWarp = userRepository.findById(id);
        if (!userEntityWarp.isPresent()) throw new ContentEmptyException("not find user");

        return User.formUserEntity(userRepository.findById(id).get());
    }

    @ExceptionHandler({OutOfIndexException.class, MethodArgumentNotValidException.class, ContentEmptyException.class})
    public ResponseEntity handleException(Exception ex) {
        Integer condition = GlobalExceptionHandler.OTHER_EXCEPTION;
        if (ex instanceof MethodArgumentNotValidException) condition =GlobalExceptionHandler.INVAILD_FOR_USER;

        log.error("Method {} error",this.getClass().getName());

        return GlobalExceptionHandler.globalExHandle(ex, condition);
    }

}
