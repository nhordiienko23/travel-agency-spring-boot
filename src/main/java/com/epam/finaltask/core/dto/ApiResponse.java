package com.epam.finaltask.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private String statusCode;
    private String statusMessage;
    private T results;

    public static <T> ApiResponse<T> ok(String message, T results) {
        return ApiResponse.<T>builder()
                .statusCode("OK")
                .statusMessage(message)
                .results(results)
                .build();
    }

    public static <T> ApiResponse<T> ok(String message) {
        return ApiResponse.<T>builder()
                .statusCode("OK")
                .statusMessage(message)
                .build();
    }
}