package org.hiber.services;

import org.hiber.entity.User;

import java.util.List;

public interface UserService {
    void create(User user);

    User findById(Integer id);

    List<User> findAll();

    void update(User user);

    void deleteById(Integer id);
}
