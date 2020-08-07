package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;

import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.GlobalExceptionHandler;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.validation.ValidationGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity registerUser(@RequestBody @Validated(ValidationGroup.class) User user) {
        userRepository.save(user.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll().stream().map(f -> User.formUserEntity(f)).collect(Collectors.toList());
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Integer id) throws OutOfIndexException, ContentEmptyException {
        if (id == null) throw new OutOfIndexException("invalid id");

         if (!userRepository.existsById(id)) throw new ContentEmptyException("not find user");

        return User.formUserEntity(userRepository.findById(id).get());
    }

    @DeleteMapping("user/{id}")
    @Transactional
    public void deleteOneById(@PathVariable Integer id) throws OutOfIndexException {
        if (id == null) throw new OutOfIndexException("invalid id");

        userRepository.deleteById(id);
    }

    @ExceptionHandler({OutOfIndexException.class, MethodArgumentNotValidException.class, ContentEmptyException.class})
    public ResponseEntity handleException(Exception ex) {
        Integer condition = GlobalExceptionHandler.OTHER_EXCEPTION;
        if (ex instanceof MethodArgumentNotValidException) condition =GlobalExceptionHandler.INVAILD_FOR_USER;

        log.error("Method {} error {}",this.getClass().getName(), ex.getClass());

        return GlobalExceptionHandler.globalExHandle(ex, condition);
    }

}
