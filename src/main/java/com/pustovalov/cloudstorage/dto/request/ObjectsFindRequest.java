package com.pustovalov.cloudstorage.dto.request;

import org.springframework.data.domain.Pageable;

public record ObjectsFindRequest(String name, String username, Pageable pageable) {

}
