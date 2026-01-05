package tech.makcymal.polylang.talks.transcribe;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.talks.TalksProperties;
import tech.makcymal.polylang.talks.transcribe.models.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RequestSet {

    private final List<List<Request>> requests = new ArrayList<>();
    private final int snippetDuration;

    public RequestSet(@NonNull TalksProperties speakingProps) {
        snippetDuration = (int) speakingProps.getTranscribeBySnippetsOfDuration().getSeconds();
    }

    public void put(@NonNull String filePath, int snippetIdx) {
        log.info("Running RequestSet.put...");

        if (requests.size() <= snippetIdx) {
            synchronized (requests) {
                if (requests.size() <= snippetIdx) {
                    requests.add(new ArrayList<>());
                }
            }
        }

        Request request = new Request(filePath, snippetDuration * snippetIdx);

        synchronized (requests.get(snippetIdx)) {
            requests.get(snippetIdx).add(request);
        }

        log.info("Running RequestSet.put...DONE");
    }

    public Optional<List<Request>> getAll(int snippetIdx) {
        log.info("Running RequestSet.getAll...");

        if (requests.size() <= snippetIdx) {
            return Optional.empty();
        }
        List<Request> requestList = requests.get(snippetIdx);
        if (requestList.isEmpty()) {
            return Optional.empty();
        }
        requests.set(snippetIdx, new ArrayList<>());

        log.info("Running RequestSet.getAll...DONE");

        return Optional.of(requestList);
    }

    public int size() {
        return requests.size();
    }

}
