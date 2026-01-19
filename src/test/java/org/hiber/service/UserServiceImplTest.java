package org.hiber.service;

import org.hiber.entity.User;
import org.hiber.repository.UserRepository;
import org.hiber.service.exceptions.BusinessException;
import org.hiber.service.exceptions.EmailAlreadyExistsException;
import org.hiber.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void deleteById_nullId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById(null));
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteById_zeroId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById(0L));
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteById_negativeId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.deleteById((long) -5));
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteById_validId_successfulDeletion() {
        long validId = 22;
        userService.deleteById(validId);
        verify(userRepository).deleteById(validId);
    }

    @Test
    void create_nullUser_throwsBusinessException() {
        User nullUser = null;
        assertThrows(BusinessException.class, () -> userService.create(nullUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_userWithEmptyName_throwsBusinessException() {
        User user = new User("", "valid@email.com", 25);
        assertThrows(BusinessException.class, () -> userService.create(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_userWithEmptyEmail_throwsBusinessException() {
        User user = new User("Valid Name", "", 25);
        assertThrows(BusinessException.class, () -> userService.create(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_userWithExistingEmail_throwsEmailAlreadyExistsException() {
        User existingUser = new User("ExampleUser", "test@example.com", 30);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        User newUser = new User("New User", "test@example.com", 25);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(newUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_validUser_callsRepositorySaveOnce() {
        User newUser = new User("ValidUser", "validemail@example.com", 25);

        when(userRepository.findByEmail("validemail@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(newUser))
                .thenReturn(newUser);

        User result = userService.create(newUser);

        assertNotNull(result);
        verify(userRepository, times(1)).save(newUser);
    }


    @Test
    void findById_invalidId_throwsBusinessException() {
        assertThrows(BusinessException.class, () -> userService.findById(null));
        assertThrows(BusinessException.class, () -> userService.findById(0L));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void findById_existingUser_returnsUser() {
        User existingUser = new User("ExistingUserName", "ExUserEmail@example.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        User result = userService.findById(1L);
        assertNotNull(result);
        assertEquals("ExistingUserName", result.getName());
        assertEquals("ExUserEmail@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_nonExistingUser_throwsUserNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(2L));
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void update_validUser_callsUpdateIfExistsOnce() {
        User user = new User("Username", "test@email.com", 25);
        user.setId(1L);
        when(userRepository.updateIfExists(1L, "Username", "test@email.com", 25))
                .thenReturn(1);

        User result = userService.update(user);

        assertNotNull(result);
        verify(userRepository, times(1))
                .updateIfExists(1L, "Username", "test@email.com", 25);
    }

    @Test
    void update_nonExistingUser_noOp() {
        User user = new User("nonExistingUser", "nonexistinguser@email.com", 25);
        user.setId(1L);
        when(userRepository.updateIfExists(1L, "nonExistingUser",
                "nonexistinguser@email.com", 25))
                .thenReturn(0);
        userService.update(user);
        verify(userRepository).updateIfExists(1L, "nonExistingUser",
                "nonexistinguser@email.com", 25);
    }

    @Test
    void update_invalidUser_throwsBusinessException() {
        User user = new User("", "test@email.com", 25);
        user.setId(1L);

        assertThrows(BusinessException.class, () -> userService.update(user));

        verifyNoInteractions(userRepository);
    }

    @Test
    void findAll_returnsListOfUsers() {
        User user1 = new User("User1", "user1@example.com", 25);
        User user2 = new User("User2", "user2@example.com", 30);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAll_returnsEmptyList_whenDaoReturnsEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<User> result = userService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }
}