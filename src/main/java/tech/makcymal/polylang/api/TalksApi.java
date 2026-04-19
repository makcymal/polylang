package tech.makcymal.polylang.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.InputStream;
import java.util.UUID;

@Tag(name = "Контроллер для работы с упражнениями по говорению")
public interface TalksApi {

    @Operation(summary = "Получить транскрипцию")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Ответ получен",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403",
                         description = "Отказ в доступе",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    String getTranscription(UUID talkId);

    @Operation(summary = "Начать новое упражнение по говорению")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Новое упражнению создано",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403",
                         description = "Отказ в доступе",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    UUID createNewTalk(UUID textId);

    @Operation(summary = "Добавить новую часть записи говорения")
    @RequestBody(content = @Content(
            mediaType = "audio/webm",
            schema = @Schema(implementation = InputStream.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Запись сохранена",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403",
                         description = "Отказ в доступе",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    void takeRecordChunk(UUID talkId, int start, InputStream chunkStream);

}
