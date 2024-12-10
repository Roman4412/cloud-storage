package com.pustovalov.cloudstorage.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@DiscriminatorValue("folder")
@Entity
public class Folder extends S3Object {

    public Folder(String name, String path, String objectKey, User user) {
        super(name, path, objectKey, user);
    }
}
