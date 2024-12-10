package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    public User register(RegistrationRequest data) {
        try {
            return repository.save(mapper.toEntity(data));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cause) {
                if (Objects.equals(cause.getConstraintName(), "users_unique_username_idx")) {
                    throw new ObjectAlreadyExistException("user %s already exist".formatted(data.username()));
                }
            }
            throw e;
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

}