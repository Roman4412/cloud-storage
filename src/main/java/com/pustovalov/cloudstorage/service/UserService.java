package com.pustovalov.cloudstorage.service;

import com.pustovalov.cloudstorage.dto.request.RegistrationRequest;
import com.pustovalov.cloudstorage.dto.response.RegistrationResponse;

public interface UserService {

    RegistrationResponse register(RegistrationRequest data);

}
