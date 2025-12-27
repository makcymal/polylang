package tech.makcymal.polylang.speaking.transcribe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.speaking.SpeakingProperties;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhisperService {

    private final SpeakingProperties speakingProps;

    private final TranscriptionRequests nonEndingSnippetRequests = new TranscriptionRequests();
    private final TranscriptionRequests endingSnippetRequests = new TranscriptionRequests();
    private final TranscriptionResponses responses = new TranscriptionResponses();

    private List<String> rawCmd;
    private int snippetDuration;

    @PostConstruct
    public void init() {
        rawCmd = List.of(
                speakingProps.getWhisperPath(),
                "--model_dir", speakingProps.getWhisperModelsDir(),
                "--model", speakingProps.getWhisperModel().getValue(),
                "-o", speakingProps.getSpeechTranscriptionsDir(),
                "-f", "json",
                "--verbose", "False",
                "--language", "en",
                "--fp16", "False",
                "--word_timestamps", "True",
                "--clip_timestamps"
        );
        snippetDuration = (int) speakingProps.getTranscribeBySnippetsOfDuration().getSeconds();
    }

    public TranscriptionDiff transcribe(String filename, int snippetIdx, boolean isEnding) {
        if (!isEnding) {
            nonEndingSnippetRequests.put(snippetIdx, filename);
        } else {
            endingSnippetRequests.put(snippetIdx, filename);
        }

        List<WhisperResponse> fileResponses = responses.getWhenAdded(filename);

        TranscriptionDiff diff = calculateDiff(snippetIdx > 0 ? fileResponses.get(snippetIdx - 1) : null, fileResponses.get(snippetIdx));
        return diff;
    }

    private TranscriptionDiff calculateDiff(WhisperResponse prevResp, WhisperResponse currResp) {
        Optional<WhisperResponse.Word> prevLastWord = Optional.ofNullable(prevResp)
                .map(WhisperResponse::getSegments)
                .map(List::getLast)
                .map(WhisperResponse.Segment::getWords)
                .map(List::getLast);

        Optional<WhisperResponse.Word> currFirstWord = Optional.ofNullable(currResp)
                .map(WhisperResponse::getSegments)
                .map(List::getFirst)
                .map(WhisperResponse.Segment::getWords)
                .map(List::getFirst);

        String text = Optional.ofNullable(currResp)
                .map(WhisperResponse::getText)
                .orElse("");
        boolean prevLastWordRevised = false;

        if (prevLastWord.isPresent() && currFirstWord.isPresent()) {
            float prevLastEnd = prevLastWord.get().getEnd();
            float prevLastProb = prevLastWord.get().getProbability();

            float currFirstStart = currFirstWord.get().getStart();
            float currFirstProb = currFirstWord.get().getProbability();

            prevLastWordRevised = currFirstStart < prevLastEnd && currFirstProb > prevLastProb;
        }

        return new TranscriptionDiff(text, prevLastWordRevised);
    }

    @Scheduled(fixedRateString = "${speaking.transcribe-by-snippets-of-duration}")
    private void scheduleTranscription() {
        log.info("Scheduled transcription");
    }

    private void runTranscription(int snippetIdx, boolean isEnding) {
        Optional<List<String>> filenames = !isEnding
                                              ? nonEndingSnippetRequests.getAll(snippetIdx)
                                              : endingSnippetRequests.getAll(snippetIdx);
        if (filenames.isEmpty()) {
            return;
        }

        boolean executed = executeWhisper(snippetIdx, isEnding, filenames.get());
        if (!executed) {

        }
    }

    private boolean executeWhisper(int snippetIdx, boolean isEnding, List<String> filenames) {
        int start = snippetDuration * snippetIdx;

        if (!isEnding) {
            return executeWhisperProcess(String.format("%d,%d", start, start + snippetDuration), filenames);
        } else {
            return executeWhisperProcess(String.format("%d", start), filenames);
        }
    }

    private boolean executeWhisperProcess(String clipTimestamps, List<String> filenames) {
        List<String> cmd = new ArrayList<>(rawCmd);
        cmd.add(clipTimestamps);
        cmd.addAll(filenames);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("executing whisper - non-zero exit code: {}", exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            log.error("executing whisper - error", e);
        }

        return true;
    }

    public void cleanup(String filename) {
        responses.cleanup(filename);
    }

}
