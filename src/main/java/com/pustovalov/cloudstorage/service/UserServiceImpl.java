package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.dto.response.RegistrationResponse;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.exception.ObjectAlreadyExistException;
import com.pustovalov.cloudstorage.mapper.UserMapper;
import com.pustovalov.cloudstorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    public RegistrationResponse register(RegistrationRequest data) {
        User savedUser;
        try {
            savedUser = repository.save(mapper.toEntity(data));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cause) {
                if (Objects.equals(cause.getConstraintName(), "users_unique_username_idx")) {
                    throw new ObjectAlreadyExistException("user %s already exist".formatted(data.username()));
                }
            }
            log.error("error when registering a user {}", data.username(), e);
            throw e;
        }
        log.debug("successful registration (username:{}, id:{})",
                  savedUser.getUsername(),
                  savedUser.getId());
        return mapper.toDto(savedUser);
    }

}