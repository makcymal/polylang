package tech.makcymal.polylang.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.makcymal.polylang.common.SerdeUtils;
import tech.makcymal.polylang.security.SecurityProperties;
import tech.makcymal.polylang.users.dto.CheckIfExistsResponse;
import tech.makcymal.polylang.users.dto.ConfirmEmailResponse;
import tech.makcymal.polylang.users.dto.LoginRequest;
import tech.makcymal.polylang.users.email_confirmation.EmailConfirmationRequest;
import tech.makcymal.polylang.users.dto.Cookies;
import tech.makcymal.polylang.users.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private static final String USER_ID_COOKIE = "UID";
    private static final String CURRENT_USER_COOKIE = "current-user";
    private static final String ACCESS_JWT_COOKIE = "access-jwt";
    private static final String REFRESH_JTI_COOKIE = "refresh-jti";
    private static final String LOGOUT_JTI_COOKIE = "logout-jti";

    private final SecurityProperties securityProps;
    private final UsersService service;

    @GetMapping("/check-if-exists/{emailOrUsername}")
    @ResponseStatus(HttpStatus.OK)
    public CheckIfExistsResponse checkIfExists(@PathVariable String emailOrUsername) {
        return service.checkIfExists(emailOrUsername);
    }

    // success -> set current-user & delete access-token, refresh-token
    // fail -> delete current-user, access-token, refresh-token
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {
        Cookies cookies;

        try {
            UserModel userModel = service.register(registerRequest);
            cookies = new Cookies(userModel, null, null);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            cookies = Cookies.ofNulls();
        }

        HttpHeaders headers = new HttpHeaders();
        setCookies(headers, cookies);
        ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.CREATED);
        return response;
    }

    // success -> set current-user, access-token, refresh-token
    // fail -> set current-user & delete access-token, refresh-token
    @PutMapping("/confirm")
    public ResponseEntity<ConfirmEmailResponse> confirmEmail(
            @RequestBody EmailConfirmationRequest emailConfirmationRequest,
            @CookieValue(name = USER_ID_COOKIE, required = false) UUID userId
    ) {
        ConfirmEmailResponse dto = new ConfirmEmailResponse(emailConfirmationRequest.getEmail(), false);
        Cookies cookies;

        try {
            cookies = service.confirmEmail(emailConfirmationRequest);
            if (cookies.getCurrentUser() != null) {
                dto.setConfirmed(cookies.getCurrentUser().isEmailConfirmed());
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            UserModel user = service.findByEmail(emailConfirmationRequest.getEmail());
            cookies = new Cookies(user, null, null);
        }

        HttpHeaders headers = new HttpHeaders();
        setCookies(headers, cookies);
        ResponseEntity<ConfirmEmailResponse> response = ResponseEntity.status(HttpStatus.OK).headers(headers).body(dto);
        return response;
    }

    // success (confirmed) -> set current-user, access-token, refresh-token
    // success (unconfirmed) -> set current-user & delete access-token, refresh-token
    // fail -> delete current-user, access-token, refresh-token
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        Cookies cookies;

        try {
            cookies = service.login(loginRequest);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            cookies = Cookies.ofNulls();
        }

        HttpHeaders headers = new HttpHeaders();
        setCookies(headers, cookies);
        ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.OK);
        return response;
    }

    // success -> set current-user, access-token, refresh-token
    // fail -> delete current-user, access-token, refresh-token
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_JTI_COOKIE, required = false) UUID refreshJti) {
        Cookies cookies;

        try {
            cookies = service.refreshTokens(refreshJti);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            cookies = Cookies.ofNulls();
        }

        HttpHeaders headers = new HttpHeaders();
        setCookies(headers, cookies);
        ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.OK);
        return response;

    }

    // success | fail -> delete current-user, access-token, refresh-token
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = LOGOUT_JTI_COOKIE, required = false) UUID refreshJti) {
        Cookies cookies = Cookies.ofNulls();
        HttpHeaders headers = new HttpHeaders();
        setCookies(headers, cookies);
        ResponseEntity<Void> response = new ResponseEntity<>(headers, HttpStatus.OK);
        return response;
    }

    private void setCookies(HttpHeaders headers, Cookies cookies) {
        if (cookies.getCurrentUser() != null) {
            setCurrentUserCookie(headers, cookies.getCurrentUser());
        } else {
            deleteCookie(headers, USER_ID_COOKIE);
            deleteCookie(headers, CURRENT_USER_COOKIE);
        }
        if (cookies.getAccessJwt() != null) {
            setAccessTokenCookie(headers, cookies.getAccessJwt());
        } else {
            deleteCookie(headers, ACCESS_JWT_COOKIE);
        }
        if (cookies.getRefreshJti() != null) {
            setRefreshTokenCookie(headers, cookies.getRefreshJti().toString());
        } else {
            deleteCookie(headers, REFRESH_JTI_COOKIE);
            deleteCookie(headers, LOGOUT_JTI_COOKIE);
        }
    }

    private void setCurrentUserCookie(HttpHeaders headers, UserModel userModel) {
        ResponseCookie userCookie = ResponseCookie.from(CURRENT_USER_COOKIE, SerdeUtils.serializeInBase64(userModel))
                .secure(true)
                .httpOnly(false)
                .sameSite("Strict")
                .path("/never-sent")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        ResponseCookie userIdCookie = ResponseCookie.from(USER_ID_COOKIE, userModel.getId().toString())
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/users/refresh")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, userCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, userIdCookie.toString());
    }

    private void setAccessTokenCookie(HttpHeaders headers, String value) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_JWT_COOKIE, value)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(securityProps.getAccessTokenValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setRefreshTokenCookie(HttpHeaders headers, String value) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_JTI_COOKIE, value)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/users/refresh")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        ResponseCookie logoutCookie = ResponseCookie.from(LOGOUT_JTI_COOKIE, value)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/users/logout")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, logoutCookie.toString());
    }


    private void deleteCookie(HttpHeaders headers, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "null")
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
