package tech.makcymal.polylang.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tech.makcymal.polylang.texts.TextDto;

@Tag(name = "Контроллер для работы с текстами для упражнений")
public interface TextsApi {

    @Operation(summary = "Получить случайный текст")
    @RequestBody(content = @Content(
            mediaType = "audio/webm",
            schema = @Schema(implementation = TextDto.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Текст получен",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    TextDto getRandomText();

}
