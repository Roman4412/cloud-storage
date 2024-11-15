package com.pustovalov.cloudstorage.repository;

import com.pustovalov.cloudstorage.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User save(User user);

}