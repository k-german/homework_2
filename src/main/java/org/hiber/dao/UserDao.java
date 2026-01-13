package org.hiber.dao;

import org.hiber.entity.User;

import java.util.List;

public interface UserDao {
    void save(User user);

    void update(User user);

    int deleteById(Long id);

    User findById(Long id);

    User findByEmail(String email);

    List<User> findAll();
}
