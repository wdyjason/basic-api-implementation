package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.validation.ValidationGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    public static List<User> userList = new ArrayList<>();

    @PostMapping("/user")
    public ResponseEntity registerUser(@RequestBody @Validated(ValidationGroup.class) User user) {
        userList.add(user);
        return new ResponseEntity(HttpStatus.OK);
    }

}
