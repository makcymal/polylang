package tech.makcymal.polylang.security.exceptions;

import org.springframework.http.HttpStatus;

public class BadTokenException extends AuthException {

    public BadTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Bad token");
    }

    public BadTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public BadTokenException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, cause);
    }

    public BadTokenException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, message, cause);
    }

}
