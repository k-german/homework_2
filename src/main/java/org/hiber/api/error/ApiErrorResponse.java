package org.hiber.api.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    @Schema(description = "Время ошибки")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус ошибки")
    private int status;

    @Schema(description = "Ошибка")
    private String error;

    @Schema(description = "Сообщение об ошибке")
    private String message;

    @Schema(description = "Путь")
    private String path;
}
