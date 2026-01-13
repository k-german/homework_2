package org.hiber.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @PositiveOrZero
    private Integer age;

}
