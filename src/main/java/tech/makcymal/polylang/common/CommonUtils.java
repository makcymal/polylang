package tech.makcymal.polylang.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

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
        StringBuilder stdoutBuilder = new StringBuilder();
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
        if (isEmpty(list)) {
            return -1;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirst(String str, Predicate<Character> predicate) {
        return findNext(str, predicate, -1);
    }

    public static int findNext(String str, Predicate<Character> predicate, int after) {
        if (!hasText(str)) {
            return -1;
        }
        for (int i = after + 1; i < str.length(); ++i) {
            if (predicate.test(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            log.error("err - sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public static <T> List<T> mutableListOf(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    public static <S, T> T mapNullable(S source, Function<S, T> mapper) {
        if (source == null) {
            return null;
        }
        return mapper.apply(source);
    }

}
