package com.nickbernard.finaltermproject;

import com.nickbernard.finaltermproject.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {

    User findByUsername(String username);
}
