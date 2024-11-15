package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.UserRegistrationData;

public interface UserService {

    void register(UserRegistrationData data);

}
