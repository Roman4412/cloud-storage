package com.pustovalov.cloudstorage.service.impl;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationData;
import com.pustovalov.cloudstorage.exception.ObjectAlreadyExistException;
import com.pustovalov.cloudstorage.repository.UserRepository;
import com.pustovalov.cloudstorage.service.UserService;
import com.pustovalov.cloudstorage.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    public void register(UserRegistrationData data) {
        try {
            repository.save(mapper.toEntity(data));
        } catch (Throwable e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ObjectAlreadyExistException("User %s already exist".formatted(data.getLogin()));
            }
            throw e;
        }
    }
}