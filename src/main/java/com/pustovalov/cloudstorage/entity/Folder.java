package com.pustovalov.cloudstorage.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DiscriminatorValue("folder")
@Entity
public class Folder extends S3Object {

    public Folder(String name, String objectKey, String parent, User user) {
        super(name, objectKey, parent, user);
    }

}