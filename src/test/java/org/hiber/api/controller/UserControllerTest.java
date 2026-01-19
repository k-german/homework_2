package org.hiber.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hiber.api.mapper.UserMapper;
import org.hiber.service.UserService;
import org.hiber.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hiber.api.dto.UserRequestDto;
import org.hiber.api.dto.UserResponseDto;
import org.hiber.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    void create_validRequest_returns201AndResponseDto() throws Exception {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("John");
        requestDto.setEmail("john@test.com");
        requestDto.setAge(30);

        User entity = new User("John", "john@test.com", 30);
        User saved = new User("John", "john@test.com", 30);
        saved.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setName("John");
        responseDto.setEmail("john@test.com");
        responseDto.setAge(30);

        when(userMapper.toEntity(any(UserRequestDto.class))).thenReturn(entity);
        when(userService.create(any(User.class))).thenReturn(saved);
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {

        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("");
        requestDto.setEmail("test@test.com");
        requestDto.setAge(20);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    void getById_existingUser_returns200AndBody() throws Exception {
        Long userId = 1L;

        User user = new User("John", "john@test.com", 30);
        user.setId(userId);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("John");
        responseDto.setEmail("john@test.com");
        responseDto.setAge(30);

        when(userService.findById(userId)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void getById_userNotFound_returns404() throws Exception {
        Long userId = 999L;

        when(userService.findById(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_usersExist_returnsList() throws Exception {
        User user1 = new User("John", "john@test.com", 30);
        user1.setId(1L);

        User user2 = new User("Jane", "jane@test.com", 25);
        user2.setId(2L);

        UserResponseDto dto1 = new UserResponseDto();
        dto1.setId(1L);
        dto1.setName("John");
        dto1.setEmail("john@test.com");
        dto1.setAge(30);

        UserResponseDto dto2 = new UserResponseDto();
        dto2.setId(2L);
        dto2.setName("Jane");
        dto2.setEmail("jane@test.com");
        dto2.setAge(25);

        when(userService.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponseDto(user1)).thenReturn(dto1);
        when(userMapper.toResponseDto(user2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("jane@test.com"));
    }

    @Test
    void getAll_noUsers_returnsEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}

