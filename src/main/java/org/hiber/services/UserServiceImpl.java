package org.hiber.services;

import org.hiber.entity.User;
import org.hiber.repository.UserRepository;
import org.hiber.services.exceptions.BusinessException;
import org.hiber.services.exceptions.EmailAlreadyExistsException;
import org.hiber.services.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

//    private final UserDao userDao;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        logger.debug("create(User user) - started: {}", user);
        validateUser(user);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("create(User user) - Email already exists: {}", user.getEmail());
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        userRepository.save(user);
        logger.info("create(User user) - successful exiting: {}", user);
        logger.debug("create(User user) - successful exiting: {}", user);
        return user;
    }

    @Override
    public User findById(Long id) {
        logger.debug("public User findById(Long id) - started, id: {}", id);
        validateId(id);
        User result = userRepository.findById(id).orElse(null);
        logger.debug("public User findById(Long id) - exiting, user: {}", result);
        return result;
    }

    @Override
    public List<User> findAll() {
        logger.debug("public List<User> findAll() - started");
        List<User> result = userRepository.findAll();
        logger.debug("public List<User> findAll() started - exiting users count: {}", result.size());
        return result;
    }

    @Override
    public User update(User user) {
        logger.debug("update(User user) - started: {}", user);
        validateUser(user);
        if (!userRepository.existsById(user.getId())) {
            logger.warn("update(User user) - fails. \n");
            throw new UserNotFoundException(user.getId());
        }

        User result = userRepository.save(user);
        logger.info("update(User user) - successful exiting: {}", user);
        logger.debug("update(User user) - successful exiting: {}", user);
        return result;
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("deleteById(Long id) - started, id: {}", id);
        validateId(id);
        if (!userRepository.existsById(id)) {
            logger.warn("deleteById(Long id) - failed.");
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        logger.info("deleteById(Long id) - success, id: {}", id);
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
