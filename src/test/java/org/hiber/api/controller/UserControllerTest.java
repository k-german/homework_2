package org.hiber.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hiber.api.mapper.UserMapper;
import org.hiber.services.UserService;
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
}

