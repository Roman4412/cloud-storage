package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.entity.User;

import java.util.Optional;

public interface UserService {

    User register(RegistrationRequest data);

    Optional<User> findByUsername(String username);

}
