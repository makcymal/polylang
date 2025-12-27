package tech.makcymal.polylang.speaking.transcribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TranscriptionRequests {

    private final List<List<String>> requests = new ArrayList<>();

    public void put(int snippetIdx, String request) {
        if (requests.size() <= snippetIdx) {
            synchronized (requests) {
                if (requests.size() <= snippetIdx) {
                    requests.add(new ArrayList<>());
                }
            }
        }
        synchronized (requests.get(snippetIdx)) {
            requests.get(snippetIdx).add(request);
        }
    }

    public Optional<List<String>> getAll(int snippetIdx) {
        if (requests.size() <= snippetIdx) {
            return Optional.empty();
        }
        List<String> result = requests.get(snippetIdx);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        requests.set(snippetIdx, new ArrayList<>());
        return Optional.of(result);
    }

}
