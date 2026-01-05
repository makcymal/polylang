package tech.makcymal.polylang.talks.transcribe.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Model {

    TINY_EN("tiny.en"),
    BASE_EN("base.en"),
    SMALL_EN("small.en");

    private final String name;

}
