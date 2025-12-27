package tech.makcymal.polylang.common.exceptions;

import lombok.Getter;

@Getter
public class JsonFileReadingException extends RuntimeException {

    public JsonFileReadingException(String filePath, Throwable cause) {
        String message = String.format(
                "err - reading json file: %s",
                filePath
        );
        super(message, cause);
    }

}
