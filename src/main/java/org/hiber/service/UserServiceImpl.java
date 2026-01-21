package org.hiber.service;

import org.hiber.entity.User;
import org.hiber.repository.UserRepository;
import org.hiber.service.exceptions.BusinessException;
import org.hiber.service.exceptions.EmailAlreadyExistsException;
import org.hiber.service.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.hiber.kafka.dto.OperationType;
import org.hiber.kafka.dto.UserNotificationEvent;
import org.hiber.kafka.producer.UserNotificationProducer;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserNotificationProducer notificationProducer;

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, UserNotificationProducer notificationProducer) {
        this.userRepository = userRepository;
        this.notificationProducer = notificationProducer;
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

        notificationProducer.send(new UserNotificationEvent(OperationType.CREATE, user.getEmail()));
        logger.info("notificationProducer send CREATE message");
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        logger.debug("public User findById(Long id) - started, id: {}", id);
        validateId(id);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.debug("public List<User> findAll() - started");
        List<User> result = userRepository.findAll();
        logger.debug("public List<User> findAll() - exiting users count: {}", result.size());
        return result;
    }

    @Override
    public User update(User user) {
        logger.debug("update(User user) - started: {}", user);
        validateUser(user);
        validateId(user.getId());

        int updated = userRepository.updateIfExists(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge()
        );

        if (updated == 0) {
            logger.debug("update - fails, user not found. id={}", user.getId());
        } else {
            logger.info("update(User user) - successful exiting: {}", user);
        }

        return user;
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("deleteById(Long id) - started, id: {}", id);
        validateId(id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String email = user.getEmail();
        logger.info("User has been found and his email has been received: {}", email);
        userRepository.delete(user);
        logger.info("User has been deleted, id: {}", id);
        notificationProducer.send(new UserNotificationEvent(OperationType.DELETE, email));
        logger.info("notificationProducer send DELETE message");
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
