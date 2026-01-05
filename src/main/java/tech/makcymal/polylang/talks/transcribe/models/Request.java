package tech.makcymal.polylang.talks.transcribe.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Request {

    private String recordFilePath;
    private int start;

}