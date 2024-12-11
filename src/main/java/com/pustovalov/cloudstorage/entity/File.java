package com.pustovalov.cloudstorage.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("file")
@Entity
public class File extends S3Object {


    private Long size;
    public File(String name, String path, String objectKey, User user, Long size) {
        super(name, path, objectKey, user);
        this.size = size;
    }
}
