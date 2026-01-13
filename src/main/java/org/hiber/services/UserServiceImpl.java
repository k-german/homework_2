package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.entity.User;
import org.hiber.services.exceptions.BusinessException;
import org.hiber.services.exceptions.EmailAlreadyExistsException;
import org.hiber.services.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    public User findById(Long id) {
        logger.debug("public User findById(Long id) - started, id: {}", id);
        validateId(id);
        User result = userDao.findById(id);
        logger.debug("public User findById(Long id) - exiting, user: {}", result);
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

        try {
            userDao.update(user);
        } catch (Exception e) {
            logger.warn("update(User user) - fails: \n{}", e.getMessage());
            throw e;
        }
        logger.info("update(User user) - successful exiting: {}", user);
        logger.debug("update(User user) - successful exiting: {}", user);
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("deleteById(Long id) - started, id: {}", id);
        validateId(id);

        try {
            int result = userDao.deleteById(id);
            if (result == 0) {
                logger.warn("deleteById(Long id) - user not found, id: {}", id);
                throw new UserNotFoundException(id);
            }
            logger.info("deleteById(Long id) - success, id: {}", id);
        } catch (BusinessException e) {
            logger.error("deleteById(Long id) - failed, id: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("deleteById(Long id) - failed. Id:{}, Exception:{}", id, e.getMessage());
            throw new BusinessException("Failed to delete user", e);
        }
        logger.debug("deleteById(Long id) - exiting");
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

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            logger.error("validateId(Long id) - invalid id: {}", id);
            throw new BusinessException("Invalid id");
        }
    }
}
