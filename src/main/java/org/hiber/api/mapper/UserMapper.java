package org.hiber.api.mapper;

import org.hiber.api.dto.UserRequestDto;
import org.hiber.api.dto.UserResponseDto;
import org.hiber.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return new User(
                dto.getName(),
                dto.getEmail(),
                dto.getAge()
        );
    }

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
