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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        assertThrows(BusinessException.class, () -> userService.deleteById(0L));
        verifyNoInteractions(userDao);
    }

    @Test
    void deleteById_negativeId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById((long) -5));
        verifyNoInteractions(userDao);
    }

    @Test
    void deleteById_validId_userNotFound_throwsUserNotFoundException() {
        long validId = 50;
        when(userDao.deleteById(validId)).thenReturn(0);
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(validId));
        verify(userDao, times(1)).deleteById(validId);
    }

    @Test
    void deleteById_validId_successfulDeletion() {
        long validId = 22;
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

    @Test
    void findById_invalidId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.findById(null));
        assertThrows(BusinessException.class, () -> userService.findById(0L));
        verify(userDao, never()).findById(any());
    }

    @Test
    void findById_existingUser_returnsUser() {
        User existingUser = new User("ExistingUserName", "ExUserEmail@example.com", 30);
        when(userDao.findById(1L)).thenReturn(existingUser);
        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("ExistingUserName", result.getName());
        assertEquals("ExUserEmail@example.com", result.getEmail());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void findById_nonExistingUser_returnsNull() {
        when(userDao.findById(2L)).thenReturn(null);
        User result = userService.findById(2L);
        assertNull(result);
        verify(userDao, times(1)).findById(2L);
    }

    @Test
    void update_nullUser_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.update(null));
        verify(userDao, never()).update(any());
    }

    @Test
    void update_userWithEmptyNameOrEmail_throwsBusinessException() {
        User userEmptyName = new User("", "testemail@example.com", 25);
        userEmptyName.setId(1L);
        User userEmptyEmail = new User("Username", "", 25);
        userEmptyEmail.setId(1L);
        assertThrows(BusinessException.class, () -> userService.update(userEmptyName));
        assertThrows(BusinessException.class, () -> userService.update(userEmptyEmail));
        verify(userDao, never()).update(any());
    }

    @Test
    void update_userWithInvalidId_throwsBusinessException() { // validateId tested
        User user = new User("Username", "testemail@example.com", 25);
        user.setId(0L);
        assertThrows(BusinessException.class, () -> userService.update(user));
        verify(userDao, never()).update(any());
    }

    @Test
    void update_daoThrowsException_propagatesException() {
        User user = new User("Username", "testemail@example.com", 25);
        user.setId(1L);
        doThrow(new RuntimeException("DB error")).when(userDao).update(user);
        assertThrows(RuntimeException.class, () -> userService.update(user));
        verify(userDao, times(1)).update(user);
    }

    @Test
    void update_validUser_callsDaoUpdateOnce() {
        User user = new User("Username", "testemail@example.com", 25);
        user.setId(1L);
        doNothing().when(userDao).update(user); // successful updating
        userService.update(user);
        verify(userDao, times(1)).update(user);
    }

    @Test
    void deleteById_invalidId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById(null));
        assertThrows(BusinessException.class, () -> userService.deleteById(0L));
        verify(userDao, never()).deleteById(any());
    }

    @Test
    void deleteById_userNotFound_throwsUserNotFoundException() {
        when(userDao.deleteById(1L)).thenReturn(0);
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(1L));
        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_daoThrowsException_propagatesBusinessException() {
        doThrow(new RuntimeException("DB error")).when(userDao).deleteById(1L);
        assertThrows(BusinessException.class, () -> userService.deleteById(1L));
        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_validId_callsDaoDeleteOnce() {
        when(userDao.deleteById(1L)).thenReturn(1);
        userService.deleteById(1L);
        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void findAll_returnsListOfUsers() {
        User user1 = new User("User1", "user1@example.com", 25);
        User user2 = new User("User2", "user2@example.com", 30);

        when(userDao.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));

        verify(userDao, times(1)).findAll();
    }

    @Test
    void findAll_returnsEmptyList_whenDaoReturnsEmpty() {
        when(userDao.findAll()).thenReturn(List.of());
        List<User> result = userService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findAll();
    }


}