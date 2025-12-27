package tech.makcymal.polylang.speaking.transcribe;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TranscriptionResponses {

    private final Map<String, List<WhisperResponse>> responses = new ConcurrentHashMap<>();

    public List<WhisperResponse> getWhenAdded(String request) {
        if (!responses.containsKey(request)) {
            responses.put(request, new ArrayList<>());
        }
        try {
            responses.get(request).wait();
            return responses.get(request);
        } catch (InterruptedException e) {
            log.error(
                    "waiting on whisper response interrupted - error: {}, filename: {}",
                    e.getMessage(),
                    request,
                    e
            );
            throw new RuntimeException(e);
        }
    }

    public void add(String request, WhisperResponse whisperResponse) {
        if (!responses.containsKey(request)) {
            responses.put(request, new ArrayList<>());
        }
        synchronized (responses.get(request)) {
            responses.get(request).add(whisperResponse);
        }
        responses.get(request).notifyAll();
    }

    public void cleanup(String request) {
        responses.remove(request);
    }

}
