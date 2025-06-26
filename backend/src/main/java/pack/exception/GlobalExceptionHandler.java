package pack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ ResponseStatusException (모든 커스텀 상태 코드 포함)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        String message = ex.getReason() != null ? ex.getReason() : "오류가 발생했습니다.";
        String errorCode;

        // HttpStatus로 변환 가능한 경우 코드명 사용
        try {
            errorCode = HttpStatus.valueOf(status).name(); // 예: NOT_FOUND
        } catch (IllegalArgumentException e) {
            errorCode = "UNKNOWN_STATUS"; // 정의되지 않은 코드
        }

        return ResponseEntity.status(status).body(
            errorResponse(errorCode, message, status)
        );
    }

    // ✅ IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
            errorResponse("INVALID_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST.value())
        );
    }

    // ✅ 예외 미지정 시 내부 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            errorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }

    // ✅ 응답 구조 통일 메서드
    private Map<String, Object> errorResponse(String errorCode, String message, int status) {
        return Map.of(
            "errorCode", errorCode,
            "message", message,
            "status", status,
            "timestamp", LocalDateTime.now()
        );
    }
}