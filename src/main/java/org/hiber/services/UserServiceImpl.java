package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.entity.User;
import org.hiber.exceptions.EmailAlreadyExistsException;

import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void create(User user) {
        validateUser(user);

        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        userDao.save(user);
    }

    @Override
    public User findById(Integer id) {
        validateId(id);
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public void update(User user) {
        validateUser(user);
        validateId(user.getId());

        User existing = userDao.findById(user.getId());
        if (existing == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!Objects.equals(existing.getEmail(), user.getEmail())
                && userDao.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        userDao.update(user);
    }

    @Override
    public void deleteById(Integer id) {
        validateId(id);

        User user = userDao.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        userDao.delete(user);
    }


    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }
    }
}
