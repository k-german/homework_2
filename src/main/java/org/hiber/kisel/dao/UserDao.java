package org.hiber.kisel.dao;

import org.hiber.kisel.entity.User;

import java.util.List;

public interface UserDao {
    void save(User user);
    void update(User user);
    void delete(User user);
    User findById(Integer id);

    User findByEmail(String email);
    List<User> findAll();
}
