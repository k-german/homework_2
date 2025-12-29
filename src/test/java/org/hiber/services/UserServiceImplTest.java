package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.entity.User;
import org.hiber.services.exceptions.EmailAlreadyExistsException;
import org.hiber.services.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserDao userDao;  // Мок DAO

    @InjectMocks
    private UserServiceImpl userService;  // юзерсервис с внедрённым мок DAO

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);  // Инициализация Mockito
    }

    @Test
    void createUser_Success() {
        User user = new User("UserOne", "UserOne@example.com", 30);

        when(userDao.findByEmail(user.getEmail())).thenReturn(null); //  email свободен

        userService.create(user);

        verify(userDao, times(1)).save(user); // тест save один раз
    }

    @Test
    void createUser_EmailAlreadyExists() {
        User user = new User("UserOne", "UserOne@example.com", 30);

        when(userDao.findByEmail(user.getEmail())).thenReturn(new User("SomeNewUser", "UserOne@example.com", 25));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.create(user)
        );

        assertEquals("Other user with email \"UserOne@example.com\" already exists in DB table.", exception.getMessage());

        verify(userDao, never()).save(any()); // save не должен вызываться
    }

    @Test
    void findById_UserExists() {
        User user = new User("UserTwo", "usertwo@example.com", 22);
        user.setId(1);

        when(userDao.findById(1)).thenReturn(user);

        User result = userService.findById(1);

        assertNotNull(result);
        assertEquals("UserTwo", result.getName());
        assertEquals("usertwo@example.com", result.getEmail());
    }

    @Test
    void findAll_ReturnsList() {
        User user = new User("User3", "user3@example.com", 25);

        when(userDao.findAll()).thenReturn(Collections.singletonList(user));

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("User3", users.get(0).getName());
    }

    @Test
    void deleteById_UserNotFound() {
        when(userDao.deleteById(1)).thenReturn(0);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteById(1)
        );

        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    void deleteById_Success() {
        when(userDao.deleteById(1)).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteById(1));

        verify(userDao, times(1)).deleteById(1);
    }
}
