package org.hiber.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;

}
