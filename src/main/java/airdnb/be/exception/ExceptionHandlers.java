package airdnb.be.exception;

import airdnb.be.web.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandlers {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpClientErrorException(HttpClientErrorException e) {
        return ApiResponse.badRequest(e.getStatusCode().toString(), e.getMessage());
    }

    /**
     * Valid/Validated 로 인한 검증에 의해 필드에 바인딩이 실패 했을 떄
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = e.getAllErrors().get(0).getDefaultMessage();
        log.warn("message : {}", message, e);

        return ApiResponse.badRequest(message);
    }

    /**
     * log level 이 ErrorCode 마다 다르기 때문에 log 는 에러가 발생한 곳에서 작성
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessEx(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.errorCode(errorCode));
    }

    /**
     * 스프링 제공 기본 응답메시지와 비즈니스 응답 메시지 통일함으로써 협업 향상을 기대.
     - 기본 에러 응답 메시지
     {
     "timestamp": "2024-08-07T08:08:19.683+00:00",
     "status": 400,
     "error": "Bad Request",
     "path": "/member/exist"
     }

     - MethodArgumentNotValid -> api 처리중, 바디의 값의 검증에서 오류임을 확인
     {
     "code": "0400",
     "status": "BAD_REQUEST",
     "message": "email 필드 오류입니다. 들어온 값 : (*@2@naver.com)"
     "data" : null
     }
     */
}
