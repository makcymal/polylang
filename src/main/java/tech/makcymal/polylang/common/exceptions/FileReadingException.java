package tech.makcymal.polylang.common.exceptions;

import lombok.Getter;

@Getter
public class FileReadingException extends RuntimeException {

    public FileReadingException(String filePath, Throwable cause) {
        String message = String.format(
                "err - reading file: %s",
                filePath
        );
        super(message, cause);
    }

}
