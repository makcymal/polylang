package me.makcymal.polylang.exceptions.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {

    private final HttpStatus httpStatus;

    public AuthException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.httpStatus = httpStatus;
    }

    public AuthException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public AuthException(HttpStatus httpStatus, Throwable cause) {
        super(cause.getMessage(), cause);
        this.httpStatus = httpStatus;
    }

    public AuthException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

}
