package org.hiber.services;

import org.hiber.dao.UserDao;
import org.hiber.services.exceptions.BusinessException;
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

}