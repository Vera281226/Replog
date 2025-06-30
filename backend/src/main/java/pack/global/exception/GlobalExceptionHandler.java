package pack.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 전역 예외 처리 클래스 (Global Exception Handler)
 *
 * 목적:
 * - 컨트롤러 전반에서 발생하는 예외를 통합적으로 처리
 * - 프론트엔드와의 통신을 고려해 JSON 응답 구조 통일
 *
 * 구조:
 * - @RestControllerAdvice: 모든 @RestController 대상 예외 처리
 * - @ExceptionHandler: 지정된 예외 클래스 발생 시 처리 메서드 실행
 * - 응답은 {errorCode, message, status, timestamp} 형식의 JSON 반환
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 예외: ResponseStatusException
     * 설명: 커스텀 상태 코드와 메시지를 포함한 예외 처리 (HttpStatus 기반)
     * 예시: throw new ResponseStatusException(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.")
     * 응답: 정의된 상태 코드 + JSON 메시지
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        int status = ex.getStatusCode().value(); // 예: 404
        String message = ex.getReason() != null ? ex.getReason() : "오류가 발생했습니다.";
        String errorCode;

        // HttpStatus 열거형으로부터 이름 추출 (예: 404 → NOT_FOUND)
        try {
            errorCode = HttpStatus.valueOf(status).name();
        } catch (IllegalArgumentException e) {
            errorCode = "UNKNOWN_STATUS"; // 정의되지 않은 상태 코드
        }

        return ResponseEntity
                .status(status) // 실제 HTTP 상태 코드로 응답
                .body(errorResponse(errorCode, message, status)); // 통일된 JSON 응답 구조 사용
    }

    /**
     * 예외: IllegalArgumentException
     * 설명: 잘못된 요청 파라미터, 검증 실패 등에서 발생
     * 예시: throw new IllegalArgumentException("ID는 0 이상이어야 합니다.")
     * 응답: 400 Bad Request + JSON 메시지
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(errorResponse("INVALID_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * 예외: Exception (모든 미처리 예외)
     * 설명: 위에서 지정하지 않은 모든 예외에 대한 안전망 처리
     * 응답: 500 Internal Server Error + 기본 메시지
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * 공통 응답 생성 메서드
     *
     * @param errorCode 오류 코드 문자열 (예: "NOT_FOUND")
     * @param message 사용자에게 표시할 메시지
     * @param status HTTP 상태 코드 값
     * @return JSON 형태의 응답 Map
     */
    private Map<String, Object> errorResponse(String errorCode, String message, int status) {
        return Map.of(
                "errorCode", errorCode,                // 에러 코드 명칭 (ex. NOT_FOUND)
                "message", message,                    // 상세 메시지
                "status", status,                      // HTTP 상태 코드
                "timestamp", LocalDateTime.now()       // 발생 시각
        );
    }
}
