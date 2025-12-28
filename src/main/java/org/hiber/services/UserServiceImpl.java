package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.entity.User;
import org.hiber.services.exceptions.BusinessException;
import org.hiber.services.exceptions.EmailAlreadyExistsException;

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

        User userWithSameEmail = userDao.findByEmail(user.getEmail());
        if (userWithSameEmail != null && !Objects.equals(userWithSameEmail.getId(), user.getId())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        userDao.update(user);
    }

    @Override
    public void deleteById(Integer id) {
        validateId(id);

        try {
            userDao.deleteById(id);
        } catch (Exception e) {
            //TODO: add exception or logging
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new BusinessException("User must not be null");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new BusinessException("Name is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessException("Email is required");
        }
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException("Invalid id");
        }
    }
}
