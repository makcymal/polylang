package tech.makcymal.polylang.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.makcymal.polylang.common.exceptions.FileReadingException;
import tech.makcymal.polylang.common.exceptions.JsonFileReadingException;
import tech.makcymal.polylang.common.exceptions.NonZeroExitCodeException;
import tech.makcymal.polylang.common.exceptions.ProcessException;
import tech.makcymal.polylang.common.exceptions.StdoutReadingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SystemUtils {

    private static final Logger log = LoggerFactory.getLogger(SystemUtils.class);
    private static final JsonMapper jsonMapper = AppConfig.getJsonMapper();

    public static String getFileNameWithoutExtension(@NonNull String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0) {
            log.warn("filePath does not have extension: {}", filePath);
            return fileName;
        }
        return fileName.substring(0, lastDot);
    }

    public static <T> T readJsonFile(@NonNull String filePath, @NonNull Class<T> clazz) {
        String content = readFile(filePath);
        try {
            return jsonMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            RuntimeException e1 = new JsonFileReadingException(filePath, e);
            log.error(e1.getMessage(), e1);
            throw e1;
        }
    }

    public static String readFile(@NonNull String filePathStr) {
        Path filePath = Paths.get(filePathStr);
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            RuntimeException e1 = new FileReadingException(filePathStr, e);
            log.error(e1.getMessage(), e);
            throw e1;
        }
    }

    public static void executeCommand(@NonNull List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        String stdout = null;

        try {
            Process process = pb.start();

            try{
                stdout = readStdout(process, cmd);
            } catch (StdoutReadingException e) {
                log.error(e.getMessage(), e);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                RuntimeException e = new NonZeroExitCodeException(cmd, exitCode, stdout);
                log.error(e.getMessage(), e);
                throw e;
            }
        } catch (IOException | InterruptedException e) {
            RuntimeException e1 = new ProcessException(cmd, stdout, e);
            log.error(e1.getMessage(), e1);
            throw e1;
        }
    }

    public static String readStdout(@NonNull Process process, @NonNull List<String> cmd) {
        StringBuffer stdoutBuilder = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stdoutBuilder.append(line);
            }
            return stdoutBuilder.toString();
        } catch (IOException e) {
            throw new StdoutReadingException(cmd, e);
        }
    }

}
