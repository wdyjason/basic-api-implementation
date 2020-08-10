package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.exception.ContentEmptyException;
import com.thoughtworks.rslist.exception.OutOfIndexException;
import com.thoughtworks.rslist.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createAUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::formUserEntity).collect(Collectors.toList());
    }

    public UserDto getUserById(Integer id) throws OutOfIndexException, ContentEmptyException {
        if (id == null) throw new OutOfIndexException("invalid id");

        if (!userRepository.existsById(id)) throw new ContentEmptyException("not find user");

        return UserDto.formUserEntity(userRepository.findById(id).get());
    }

    @Transactional
    public void deleteUserById(Integer id) throws OutOfIndexException {
        if (id == null) throw new OutOfIndexException("invalid id");
        userRepository.deleteById(id);
    }
}
