package tech.makcymal.polylang.common.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class NonZeroExitCodeException extends RuntimeException {

    public NonZeroExitCodeException(List<String> cmd, int exitCode, String stdout) {
        String message = String.format(
                "err - process execution exits with non-zero code: [%d], cmd: [%s], stdout: [%s]",
                exitCode,
                cmd.toString(),
                stdout
        );
        super(message);
    }

}
