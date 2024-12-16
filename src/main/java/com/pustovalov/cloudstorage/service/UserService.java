package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationRequest;
import com.pustovalov.cloudstorage.entity.User;

import java.util.Optional;

public interface UserService {

    void register(UserRegistrationRequest data);

    Optional<User> findByUsername(String username);

}
