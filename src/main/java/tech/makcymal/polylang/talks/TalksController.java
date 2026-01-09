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
import tech.makcymal.polylang.security.context.JwtAuthHolder;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
public class TalksController {

    private final JwtAuthHolder authHolder;
    private final TalksService service;

    @PostMapping("/{textId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createNewTalk(@PathVariable UUID textId) {
        return service.createNewTalk(textId, authHolder.get().getUserId(), authHolder.get().getClientId());
    }

    @PutMapping(value = "/record/{talkId}/{start}",
                consumes = "audio/webm")
    @ResponseStatus(HttpStatus.OK)
    public void takeRecordChunkV2(@PathVariable UUID talkId, @PathVariable int start, InputStream chunkStream) {
        service.submitTranscriptionTask(talkId, start, chunkStream);
    }

    @GetMapping("/transcription/{talkId}")
    @ResponseStatus(HttpStatus.OK)
    public String getTranscription(@PathVariable UUID talkId) {
        return service.getTranscription(talkId);
    }

}
