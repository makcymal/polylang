package tech.makcymal.polylang.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.makcymal.polylang.common.SerdeUtils;
import tech.makcymal.polylang.security.SecurityProperties;
import tech.makcymal.polylang.users.dto.CheckIfExistsResponse;
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

    private static final String CURRENT_USER_COOKIE = "current-user";
    private static final String ACCESS_JWT_COOKIE = "access-jwt";
    private static final String REFRESH_JTI_COOKIE = "refresh-jti";

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
            cookies = Cookies.ofNulls();
        }

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.CREATED);
        setCookies(response, cookies);
        return response;
    }

    // success -> set current-user, access-token, refresh-token
    // fail -> set current-user & delete access-token, refresh-token
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmEmail(@RequestBody EmailConfirmationRequest emailConfirmationRequest) {
        Cookies cookies;

        try {
            cookies = service.confirmEmail(emailConfirmationRequest);
        } catch (RuntimeException e) {
            UserModel user = service.findByEmail(emailConfirmationRequest.getEmail());
            cookies = new Cookies(user, null, null);
        }

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        setCookies(response, cookies);
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
            cookies = Cookies.ofNulls();
        }

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        setCookies(response, cookies);
        return response;
    }

    // success -> set current-user, access-token, refresh-token
    // fail -> delete current-user, access-token, refresh-token
    @PostMapping("/forget/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_JTI_COOKIE, required = false) UUID refreshJti) {
        Cookies cookies;

        try {
            cookies = service.refreshTokens(refreshJti);
        } catch (RuntimeException e) {
            cookies = Cookies.ofNulls();
        }

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        setCookies(response, cookies);
        return response;

    }

    // success | fail -> delete current-user, access-token, refresh-token
    @PostMapping("/forget/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_JTI_COOKIE, required = false) UUID refreshJti) {
        Cookies cookies = Cookies.ofNulls();
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);
        setCookies(response, cookies);
        return response;
    }

    private <T> void setCookies(ResponseEntity<T> response, Cookies cookies) {
        if (cookies.getCurrentUser() != null) {
            setCurrentUserCookie(response, cookies.getCurrentUser());
        } else {
            deleteCookie(response, CURRENT_USER_COOKIE);
        }
        if (cookies.getAccessJwt() != null) {
            setAccessTokenCookie(response, cookies.getAccessJwt());
        } else {
            deleteCookie(response, ACCESS_JWT_COOKIE);
        }
        if (cookies.getRefreshJti() != null) {
            setRefreshTokenCookie(response, cookies.getRefreshJti().toString());
        } else {
            deleteCookie(response, REFRESH_JTI_COOKIE);
        }
    }

    private <T> void setCurrentUserCookie(ResponseEntity<T> response, UserModel userModel) {
        String value = SerdeUtils.serialize(userModel);
        ResponseCookie cookie = ResponseCookie.from(CURRENT_USER_COOKIE, value)
                .secure(true)
                .httpOnly(false)
                .sameSite("Strict")
                .path("/never-sent")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private <T> void setAccessTokenCookie(ResponseEntity<T> response, String value) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_JWT_COOKIE, value)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(securityProps.getAccessTokenValidity())
                .build();
        response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private <T> void setRefreshTokenCookie(ResponseEntity<T> response, String value) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_JTI_COOKIE, value)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/users/forget")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    private <T> void deleteCookie(ResponseEntity<T> response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "null")
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
