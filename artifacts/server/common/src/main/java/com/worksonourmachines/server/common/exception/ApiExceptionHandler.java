package com.worksonourmachines.server.common.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        HandlerMethodValidationException.class,
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class
    })
    ResponseEntity<ApiErrorResponse> badRequest() {
        return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiErrorResponse> responseStatus(ResponseStatusException exception) {
        HttpStatusCode statusCode = exception.getStatusCode();
        String message = exception.getReason();

        return ResponseEntity.status(statusCode).body(error(statusCode, message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiErrorResponse> accessDenied() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error(HttpStatus.UNAUTHORIZED));
    }

    public static ApiErrorResponse error(HttpStatusCode statusCode) {
        return error(statusCode, null);
    }

    public static ApiErrorResponse error(HttpStatusCode statusCode, String message) {
        return new ApiErrorResponse(code(statusCode), message == null || message.isBlank() ? defaultMessage(statusCode) : message);
    }

    private static String code(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 400 -> "bad_request";
            case 401 -> "unauthorized";
            case 403 -> "forbidden";
            case 404 -> "not_found";
            default -> "error";
        };
    }

    private static String defaultMessage(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 400 -> "The server could not understand the request due to invalid syntax.";
            case 401 -> "Access is unauthorized.";
            case 403 -> "Access is forbidden.";
            case 404 -> "The requested resource was not found.";
            default -> "An unexpected error occurred.";
        };
    }
}
