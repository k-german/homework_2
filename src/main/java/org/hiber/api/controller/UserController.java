package org.hiber.api.controller;

import jakarta.validation.Valid;
import org.hiber.api.dto.UserRequestDto;
import org.hiber.api.dto.UserResponseDto;
import org.hiber.api.mapper.UserMapper;
import org.hiber.entity.User;
import org.hiber.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Valid @RequestBody UserRequestDto requestDto) {
        User user = userMapper.toEntity(requestDto);
        User created = userService.create(user);
        return userMapper.toResponseDto(created);
    }

    @GetMapping("/{id}")
    public UserResponseDto findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return userMapper.toResponseDto(user);
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @PutMapping("/{id}")
    public UserResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        User user = userMapper.toEntity(requestDto);
        user.setId(id);
        User updated = userService.update(user);
        return userMapper.toResponseDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
