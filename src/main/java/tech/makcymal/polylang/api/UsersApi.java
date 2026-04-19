package tech.makcymal.polylang.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import tech.makcymal.polylang.users.dto.CheckIfExistsResponse;
import tech.makcymal.polylang.users.dto.EmailConfirmationResponse;
import tech.makcymal.polylang.users.dto.LoginRequest;
import tech.makcymal.polylang.users.dto.RegisterRequest;
import tech.makcymal.polylang.users.email_confirmation.EmailConfirmationRequest;

import java.util.UUID;

@Tag(name = "Контроллер для регистрации, аутентификации, поиска и обновления пользователей")
public interface UsersApi {

    @Operation(summary = "Проверить, что пользователь с данным email или username уже существует")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Ответ получен",
                         content = @Content(schema = @Schema(implementation = CheckIfExistsResponse.class))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    CheckIfExistsResponse checkIfExists(String emailOrUsername);

    @Operation(summary = "Зарегистрировать нового пользователя")
    @RequestBody(content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = RegisterRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                         description = "Пользователь зарегистрирован",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<Void> register(RegisterRequest request);

    @Operation(summary = "Подтвердить почту пользователя")
    @RequestBody(content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = EmailConfirmationRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Почта подтверждена",
                         content = @Content(schema = @Schema(implementation = EmailConfirmationResponse.class))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<EmailConfirmationResponse> confirmEmail(EmailConfirmationRequest request, UUID emailConfirmationCodeId);

    @Operation(summary = "Аутентификация пользователя")
    @RequestBody(content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Пользователь аутентифицирован",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<Void> login(LoginRequest request);

    @Operation(summary = "Обновить пару refresh-access токенов")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Токены обновлены",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<Void> refresh(UUID refreshJti);

    @Operation(summary = "Удалить токены аутентификации")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Токены удалены",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400",
                         description = "Ошибка валидации запроса",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401",
                         description = "Ошибка аутентификации",
                         content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "503",
                         description = "Сервер недоступен",
                         content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<Void> logout(UUID logoutJti);

}
