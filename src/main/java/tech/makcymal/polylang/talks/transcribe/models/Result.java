package tech.makcymal.polylang.talks.transcribe.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class Result {

    Request request;
    Optional<Response> response;
    Optional<Throwable> executionError;
    Optional<Throwable> readingError;

}
