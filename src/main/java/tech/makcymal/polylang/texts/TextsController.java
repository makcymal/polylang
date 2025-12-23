package tech.makcymal.polylang.texts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/texts")
@RequiredArgsConstructor
public class TextsController {

    private final TextsService service;
    private final TextsMapper mapper;

    @GetMapping("/random")
    public TextDto getRandomText() {
        TextEntity text = service.getRandomText();
        return mapper.toDto(text);
    }

}
