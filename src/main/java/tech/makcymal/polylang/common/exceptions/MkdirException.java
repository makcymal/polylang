package tech.makcymal.polylang.common.exceptions;

import lombok.Getter;

@Getter
public class MkdirException extends RuntimeException {

    public MkdirException(String dirPath, Throwable cause) {
        String message = String.format(
                "err - cannot create directory %s",
                dirPath
        );
        super(message, cause);
    }

}
