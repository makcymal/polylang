package tech.makcymal.polylang.common.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class StdoutReadingException extends RuntimeException {

    public StdoutReadingException(List<String> cmd, Throwable cause) {
        String message = String.format("err - reading stdout of process execution, cmd: [%s]", cmd.toString());
        super(message, cause);
    }

}
