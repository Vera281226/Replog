package pack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        String message = safeToString(ex.getReason() != null ? ex.getReason() : "오류가 발생했습니다.");
        String errorCode;
        try {
            errorCode = HttpStatus.valueOf(status).name();
        } catch (IllegalArgumentException e) {
            errorCode = "UNKNOWN_STATUS";
        }
        return ResponseEntity
                .status(status)
                .body(errorResponse(errorCode, message, status));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(errorResponse("INVALID_ARGUMENT", safeToString(ex.getMessage()), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Map을 사람이 읽기 쉬운 String으로 변환
        StringBuilder sb = new StringBuilder();
        errors.forEach((field, msg) -> sb.append(field).append(": ").append(msg).append("; "));
        String errorMsg = sb.length() > 0 ? sb.toString().trim() : "입력값이 올바르지 않습니다.";

        return ResponseEntity
                .badRequest()
                .body(errorResponse("VALIDATION_FAILED", errorMsg, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    // message를 항상 String으로 변환 (null, 객체 모두 방어)
    private Map<String, Object> errorResponse(String errorCode, Object message, int status) {
        return Map.of(
                "errorCode", errorCode,
                "message", safeToString(message),
                "status", status,
                "timestamp", LocalDateTime.now()
        );
    }

    // null, 객체, String 모두 안전하게 변환
    private String safeToString(Object obj) {
        if (obj == null) return "";
        if (obj instanceof String) return (String) obj;
        return obj.toString();
    }
}