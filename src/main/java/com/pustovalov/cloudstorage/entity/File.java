package com.pustovalov.cloudstorage.entity;

import jakarta.persistence.DiscriminatorValue;

@DiscriminatorValue("file")
public class File extends S3Object {
}
