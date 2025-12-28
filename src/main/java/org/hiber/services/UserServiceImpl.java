package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.entity.User;
import org.hiber.services.exceptions.BusinessException;
import org.hiber.services.exceptions.EmailAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void create(User user) {
        logger.debug("create(User user) - started: {}", user);
        validateUser(user);

        if (userDao.findByEmail(user.getEmail()) != null) {
            logger.warn("create(User user) - Email already exists: {}", user.getEmail());
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        userDao.save(user);
        logger.info("create(User user) - successful exiting: {}", user);
        logger.debug("create(User user) - successful exiting: {}", user);
    }

    @Override
    public User findById(Integer id) {
        logger.debug("public User findById(Integer id) - started, id: {}", id);
        validateId(id);
        User result = userDao.findById(id);
        logger.debug("public User findById(Integer id) - exiting, user: {}", result);
        return result;
    }

    @Override
    public List<User> findAll() {
        logger.debug("public List<User> findAll() - started");
        List<User> result = userDao.findAll();
        logger.debug("public List<User> findAll() started - exiting users count: {}", result.size());
        return result;
    }

    @Override
    public void update(User user) {
        logger.debug("update(User user) - started: {}", user);
        validateUser(user);
        validateId(user.getId());

        User userWithSameEmail = userDao.findByEmail(user.getEmail());
        if (userWithSameEmail != null && !Objects.equals(userWithSameEmail.getId(), user.getId())) {
            logger.warn("update(User user) - Email already exists: {}", user.getEmail());
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        userDao.update(user);
        logger.info("update(User user) - successful exiting: {}", user);
        logger.debug("update(User user) - successful exiting: {}", user);
    }

    @Override
    public void deleteById(Integer id) {
        logger.debug("deleteById(Integer id) - started, id: {}", id);
        validateId(id);

        try {
            userDao.deleteById(id);
            logger.info("deleteById(Integer id) - success, id: {}", id);
        } catch (Exception e) {
            logger.error("deleteById(Integer id) - failed, id: {}", id, e);
            throw e;
        }
        logger.debug("deleteById(Integer id) - exiting");
    }

    private void validateUser(User user) {
        if (user == null) {
            logger.error("validateUser(User user) - User == null");
            throw new BusinessException("User must not be null");
        }
        if (user.getName().isBlank()) {
            logger.error("validateUser(User user) - user.getName() == (null or empty)");
            throw new BusinessException("Name is required");
        }
        if (user.getEmail().isBlank()) {
            logger.error("validateUser(User user) - user.getEmail() == (null or empty)");
            throw new BusinessException("Email is required");
        }
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            logger.error("validateId(Integer id) - invalid id: {}", id);
            throw new BusinessException("Invalid id");
        }
    }
}
