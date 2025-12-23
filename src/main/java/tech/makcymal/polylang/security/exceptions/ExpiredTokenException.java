package tech.makcymal.polylang.security.exceptions;

import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends AuthException {

    public ExpiredTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Expired token");
    }

    public ExpiredTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public ExpiredTokenException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, cause);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }

}
