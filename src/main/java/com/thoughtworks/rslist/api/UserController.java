package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.validation.ValidationGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    public static List<User> userList = new ArrayList<>();

    @PostMapping("/user")
    public ResponseEntity registerUser(@RequestBody @Validated(ValidationGroup.class) User user) {
        userList.add(user);
        return new ResponseEntity("index: " + (userList.size() - 0), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userList;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleException(MethodArgumentNotValidException ex) {
        CommonError error = new CommonError();
        error.setError("invalid user");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
