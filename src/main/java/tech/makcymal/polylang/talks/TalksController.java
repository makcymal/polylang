package tech.makcymal.polylang.talks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tech.makcymal.polylang.api.TalksApi;
import tech.makcymal.polylang.security.context.JwtAuthHolder;
import tech.makcymal.polylang.talks.transcription.WhisperOutput;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static java.lang.Double.parseDouble;
import static java.lang.Math.round;

@Slf4j
@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
public class TalksController implements TalksApi {

    private final JwtAuthHolder authHolder;
    private final TalksService service;

    @Override
    @GetMapping("/transcription/{talkId}")
    @ResponseStatus(HttpStatus.OK)
    public String getTranscription(@PathVariable UUID talkId) {
        return service.getTranscription(talkId);
    }

    @Override
    @PostMapping("/{textId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createNewTalk(@PathVariable UUID textId) {
        return service.createNewTalk(textId, authHolder.get().getUserId(), authHolder.get().getClientId());
    }

    @Override
    @PutMapping(value = "/record/{talkId}/{startStr}", consumes = "audio/webm")
    @ResponseStatus(HttpStatus.OK)
    public void takeRecordChunk(@PathVariable UUID talkId, @PathVariable String startStr, InputStream chunkStream) {
        int start = (int) round(parseDouble(startStr));
        service.submitTranscriptionTask(talkId, start, chunkStream);
    }

    @GetMapping("/analyze/{talkId}")
    @ResponseStatus(HttpStatus.OK)
    public String analyze(@PathVariable UUID talkId) {
        return service.analyze(talkId);
    }

}
