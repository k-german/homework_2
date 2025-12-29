package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.entity.User;
import org.hiber.services.exceptions.BusinessException;
import org.hiber.services.exceptions.EmailAlreadyExistsException;
import org.hiber.services.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void deleteById_nullId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById(null));
        verifyNoInteractions(userDao);
    }

    @Test
    void deleteById_zeroId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById(0));
        verifyNoInteractions(userDao);
    }

    @Test
    void deleteById_negativeId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById(-5));
        verifyNoInteractions(userDao);
    }

    @Test
    void deleteById_validId_userNotFound_throwsUserNotFoundException() {
        Integer validId = 50;
        when(userDao.deleteById(validId)).thenReturn(0);
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(validId));
        verify(userDao, times(1)).deleteById(validId);
    }

    @Test
    void deleteById_validId_successfulDeletion() {
        Integer validId = 22;
        when(userDao.deleteById(validId)).thenReturn(1);
        userService.deleteById(validId);
        verify(userDao, times(1)).deleteById(validId);
    }

    @Test
    void create_nullUser_throwsBusinessException() {
        User nullUser = null;
        assertThrows(BusinessException.class, () -> userService.create(nullUser));
        verify(userDao, never()).save(any());
    }

    @Test
    void create_userWithEmptyName_throwsBusinessException() {
        User user = new User("", "valid@email.com", 25);
        assertThrows(BusinessException.class, () -> userService.create(user));
        verify(userDao, never()).save(any());
    }

    @Test
    void create_userWithEmptyEmail_throwsBusinessException() {
        User user = new User("Valid Name", "", 25);
        assertThrows(BusinessException.class, () -> userService.create(user));
        verify(userDao, never()).save(any());
    }

    @Test
    void create_userWithExistingEmail_throwsEmailAlreadyExistsException() {
        User existingUser = new User("ExampleUser", "test@example.com", 30);
        when(userDao.findByEmail("test@example.com")).thenReturn(existingUser);
        User newUser = new User("New User", "test@example.com", 25);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(newUser));
        verify(userDao, never()).save(any());
    }

    @Test
    void create_validUser_callsDaoSaveOnce() {
        User newUser = new User("ValidUser", "validemail@example.com", 25);
        when(userDao.findByEmail("validemail@example.com")).thenReturn(null);
        userService.create(newUser);
        verify(userDao, times(1)).save(newUser);
    }


}