package tech.makcymal.polylang.common.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ProcessException extends RuntimeException {

    public ProcessException(List<String> cmd, String stdout, Throwable cause) {
        String message = String.format(
                "err - process execution, cmd: [%s], stdout: [%s]",
                cmd.toString(),
                stdout
        );
        super(message, cause);
    }

}
