package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationRequest;
import com.pustovalov.cloudstorage.entity.User;
import com.pustovalov.cloudstorage.mapper.UserMapper;
import com.pustovalov.cloudstorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    private final RootFolderInitializer rootFolderInitializer;

    @Transactional
    public void register(UserRegistrationRequest data) {
        User registeredUser = repository.save(mapper.toEntity(data));
        rootFolderInitializer.initialize(registeredUser);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

}