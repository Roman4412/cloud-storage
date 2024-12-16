package com.pustovalov.cloudstorage.repository;

import com.pustovalov.cloudstorage.entity.S3Object;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface S3ObjectRepository extends CrudRepository<S3Object, Long> {

    Optional<S3Object> findByObjectKey(String objectKey);

    List<S3Object> findAllByParentStartsWith(String objectKey);

    List<S3Object> findAllByParent(String objectKey);

    Optional<S3Object> findByUserUsernameAndObjectKeyEndsWith(String username, String objectKeyPart);

    void deleteAllByParentStartsWith(String objectKey);

}
