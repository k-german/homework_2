package org.hiber.services;

import org.hiber.entity.User;

import java.util.List;

public interface UserService {
    void create(User user);

    User findById(Long id);

    List<User> findAll();

    void update(User user);

    void deleteById(Long id);
}
