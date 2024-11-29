package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;

public interface UserService {

    void register(RegistrationRequest data);

}
