package com.pustovalov.cloudstorage.repository;

import com.pustovalov.cloudstorage.entity.S3Object;
import com.pustovalov.cloudstorage.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface S3ObjectPagingRepository extends PagingAndSortingRepository<S3Object, Long> {

    List<S3Object> findAllByUserAndNameContains(User user, String name, Pageable pageable);

}
