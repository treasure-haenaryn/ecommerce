package com.example.msakafka.order.interfaces.web;

import java.time.Instant;

/**
 * 모든 REST API 응답이 공통으로 따르는 응답 껍데기.
 * 성공/실패는 HTTP 상태 코드로 판단하고, 여기서는 상태 코드만으로 알 수 없는 부가 정보만 담는다.
 */
public record ApiResponse<T>(
        T data,
        String message,
        Instant timestamp
) {

    public static <T> ApiResponse<T> of(T data, String message) {
        return new ApiResponse<>(data, message, Instant.now());
    }

    public static <T> ApiResponse<T> of(T data) {
        return of(data, null);
    }
}
