package tech.makcymal.polylang.texts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.makcymal.polylang.api.TextsApi;

@Slf4j
@RestController
@RequestMapping("/texts")
@RequiredArgsConstructor
public class TextsController implements TextsApi {

    private final TextsService service;

    @Override
    @GetMapping("/random")
    public TextDto getRandomText() {
        return service.getRandomText();
    }

}
