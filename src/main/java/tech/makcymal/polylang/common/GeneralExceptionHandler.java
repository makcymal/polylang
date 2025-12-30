package tech.makcymal.polylang.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.makcymal.polylang.common.exceptions.HttpException;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(HttpException e) {
        log.error("err - http exception, status: [{}], message: [{}]", e.getHttpStatus(), e.getMessage(), e);
        ErrorResponse err = new ErrorResponse(e.getHttpStatus().value() * 10, e.getMessage());
        return new ResponseEntity<>(err, e.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("err - unexpected exception: [{}]", e.getMessage(), e);
        ErrorResponse err = new ErrorResponse(5000, "Unexpected error");
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
