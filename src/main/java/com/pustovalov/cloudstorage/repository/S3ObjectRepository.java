package com.pustovalov.cloudstorage.repository;

import com.pustovalov.cloudstorage.entity.S3Object;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface S3ObjectRepository extends CrudRepository<S3Object, Long> {

    List<S3Object> findAllByPath(String path);

    List<S3Object> findAllByPathStartsWith(String path);

    Optional<S3Object> findByPathAndName(String path, String name);

    void deleteAllByPathStartingWith(String path);

    void deleteByPathAndName(String path, String name);

    void deleteById(Long id);
}
