package org.hiber.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserResponseDto {

    @Schema(description = "id пользователя")
    private Long id;

    @Schema(description = "Имя пользователя")
    private String name;

    @Schema(description = "Email пользователя")
    private String email;

    @Schema(description = "Возраст пользователя")
    private Integer age;

    @Schema(description = "Время создания элемента")
    private LocalDateTime createdAt;

}
