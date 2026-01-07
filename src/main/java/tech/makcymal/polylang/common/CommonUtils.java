package tech.makcymal.polylang.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import tech.makcymal.polylang.common.exceptions.FileReadingException;
import tech.makcymal.polylang.common.exceptions.JsonFileReadingException;
import tech.makcymal.polylang.common.exceptions.MkdirException;
import tech.makcymal.polylang.common.exceptions.NonZeroExitCodeException;
import tech.makcymal.polylang.common.exceptions.ProcessException;
import tech.makcymal.polylang.common.exceptions.StdoutReadingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;

public class CommonUtils {

    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);
    private static final JsonMapper jsonMapper = AppConfig.getJsonMapper();

    public static void mkdir(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            RuntimeException e1 = new MkdirException(path, e);
            log.error(e1.getMessage(), e1);
            throw e1;
        }
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

    public static <T> String objToJson(T object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String objToBase64Json(T object) {
        return Base64.getEncoder().encodeToString(objToJson(object).getBytes());
    }

    public static <T> int findFirst(List<T> list, Predicate<T> predicate) {
        if (CollectionUtils.isEmpty(list)) {
            return -1;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

}
