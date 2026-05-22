package kz.iitu.hello.exception;

import jakarta.servlet.http.HttpServletRequest;
import kz.iitu.hello.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(buildErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleConflict(BusinessException ex, HttpServletRequest request) {
        log.error("Business conflict: {}", ex.getMessage());
        return ResponseEntity.status(409).body(buildErrorResponse(409, "Conflict", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler({CourseLimitExceededException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorResponse(400, "Bad Request", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("Validation failed at {}: {}", request.getRequestURI(), validationErrors);
        return ResponseEntity.badRequest().body(buildErrorResponse(400, "Bad Request", "Validation failed", request.getRequestURI(), validationErrors));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(404).body(buildErrorResponse(404, "Not Found", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(buildErrorResponse(500, "Internal Server Error", "An unexpected error occurred", request.getRequestURI(), null));
    }

    private ErrorResponse buildErrorResponse(int status,
                                             String error,
                                             String message,
                                             String path,
                                             Map<String, String> validationErrors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, validationErrors);
    }
}
