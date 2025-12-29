package org.hiber.dao;

import org.hiber.entity.User;

import java.util.List;

public interface UserDao {
    void save(User user);

    void update(User user);

    int deleteById(Integer id);

    User findById(Integer id);

    User findByEmail(String email);

    List<User> findAll();
}
