package com.pustovalov.cloudstorage.mapper;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.dto.response.RegistrationResponse;
import com.pustovalov.cloudstorage.entity.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    public abstract User toEntity(RegistrationRequest request);

    public abstract RegistrationResponse toDto(User user);

    @Named("encodePassword")
    protected String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}