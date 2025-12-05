package me.makcymal.polylang.exceptions.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpException extends RuntimeException {

    private final HttpStatus httpStatus;

    public HttpException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.httpStatus = httpStatus;
    }

    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpException(HttpStatus httpStatus, Throwable cause) {
        super(cause.getMessage(), cause);
        this.httpStatus = httpStatus;
    }

    public HttpException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

}
