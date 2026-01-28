package org.hiber.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestDto {

    @Schema(description = "Имя пользователя")
    @NotBlank(message = "name must not be blank")
    private String name;

    @Schema(description = "Email пользователя")
    @NotBlank(message = "email must not be blank")
    @Email(message = "is not valid")
    private String email;

    @Schema(description = "Возраст пользователя")
    @PositiveOrZero(message = "must be >= 0")
    private Integer age;

}
