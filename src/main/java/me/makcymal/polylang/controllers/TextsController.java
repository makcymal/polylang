package me.makcymal.polylang.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.makcymal.polylang.dtos.TextDto;
import me.makcymal.polylang.entities.Text;
import me.makcymal.polylang.mappers.TextsMapper;
import me.makcymal.polylang.services.TextsService;
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
        Text text = service.getRandomText();
        return mapper.toDto(text);
    }

}
