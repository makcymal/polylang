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
import tech.makcymal.polylang.common.exceptions.HttpException;
import tech.makcymal.polylang.security.SecurityProperties;
import tech.makcymal.polylang.security.exceptions.AuthException;
import tech.makcymal.polylang.users.dto.CheckIfExistsResponse;
import tech.makcymal.polylang.users.dto.ConfirmEmailResponse;
import tech.makcymal.polylang.users.dto.LoginRequest;
import tech.makcymal.polylang.users.dto.Session;
import tech.makcymal.polylang.users.email_confirmation.EmailConfirmationRequest;
import tech.makcymal.polylang.users.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static tech.makcymal.polylang.common.CommonUtils.objToBase64Json;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private static final String EMAIL_CONFIRMATION_CODE_ID_COOKIE = "ecc-id";
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

    // success -> set current-user, ecc-id & delete access-jwt, refresh-token, logout-jti
    // fail -> delete all
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        try {
            Session session = service.register(registerRequest);
            setCurrentUserCookie(headers, session.getCurrentUser());
            setEmailConfirmationCodeIdCookie(headers, session.getEmailConfirmationCodeId());
            deleteAccessJwtCookie(headers);
            deleteRefreshJtiCookie(headers);
            status = HttpStatus.CREATED;

        } catch (HttpException e) {
            throw e;

        } catch (RuntimeException e) {
            if (!(e instanceof AuthException)) {
                log.error(e.getMessage(), e);
            }
            deleteAllCookies(headers);
        }

        return new ResponseEntity<>(headers, status);
    }

    // success (confirmed) -> set current-user, access-jwt, refresh-jti, logout-jti & delete ecc-id
    // success (unconfirmed) | fail -> set current-user, ecc-id & delete access-jwt, refresh-jti, logout-jti
    @PutMapping("/confirm")
    public ResponseEntity<ConfirmEmailResponse> confirmEmail(
            @RequestBody EmailConfirmationRequest request,
            @CookieValue(name = EMAIL_CONFIRMATION_CODE_ID_COOKIE, required = false) UUID emailConfirmationCodeId
    ) {
        ConfirmEmailResponse dto = new ConfirmEmailResponse(request.getEmail(), false);
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        try {
            if (emailConfirmationCodeId == null) {
                throw new HttpException(HttpStatus.UNAUTHORIZED, "Cookie ecc-id is missing");
            }
            Session session = service.confirmEmail(request);
            if (session.getCurrentUser() != null) {
                dto.setConfirmed(session.getCurrentUser().isEmailConfirmed());
            }
            if (!dto.isConfirmed()) {
                throw new AuthException(HttpStatus.UNAUTHORIZED, "User still not confirmed");
            }

            setCurrentUserCookie(headers, session.getCurrentUser());
            setAccessJwtCookie(headers, session.getAccessJwt());
            setRefreshJtiCookie(headers, session.getRefreshJti());
            deleteEmailConfirmationCodeIdCookie(headers);
            status = HttpStatus.OK;

        } catch (HttpException e) {
            throw e;

        } catch (RuntimeException e) {
            if (!(e instanceof AuthException)) {
                log.error(e.getMessage(), e);
            }
            Session session = service.provideEmailConfirmation(request.getEmail());
            setCurrentUserCookie(headers, session.getCurrentUser());
            setEmailConfirmationCodeIdCookie(headers, session.getEmailConfirmationCodeId());
            deleteAccessJwtCookie(headers);
            deleteRefreshJtiCookie(headers);
        }

        return ResponseEntity.status(status).headers(headers).body(dto);
    }

    // success (confirmed) -> set current-user, access-jwt, refresh-jti, logout-jti & delete ecc-id
    // success (unconfirmed) -> set current-user, ecc-id & delete access-jwt, refresh-jti, logout-jti
    // fail -> delete current-user, ecc-id, access-jwt, refresh-jti, logout-jti
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        try {
            Session session = service.login(loginRequest);
            setCurrentUserCookie(headers, session.getCurrentUser());
            if (session.getCurrentUser().isEmailConfirmed()) {
                setAccessJwtCookie(headers, session.getAccessJwt());
                setRefreshJtiCookie(headers, session.getRefreshJti());
                deleteEmailConfirmationCodeIdCookie(headers);
            } else {
                setEmailConfirmationCodeIdCookie(headers, session.getEmailConfirmationCodeId());
                deleteAccessJwtCookie(headers);
                deleteRefreshJtiCookie(headers);
            }
            status = HttpStatus.OK;

        } catch (HttpException e) {
            throw e;

        } catch (RuntimeException e) {
            if (!(e instanceof AuthException)) {
                log.error(e.getMessage(), e);
            }
            deleteAllCookies(headers);
        }

        return new ResponseEntity<>(headers, status);
    }

    // success -> set current-user, access-jwt, refresh-jti, logout-jti & delete ecc-id
    // fail -> delete current-user, ecc-id, access-jwt, refresh-jti, logout-jti
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(name = REFRESH_JTI_COOKIE, required = false) UUID refreshJti) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        try {
            Session session = service.refreshTokens(refreshJti);
            setCurrentUserCookie(headers, session.getCurrentUser());
            setAccessJwtCookie(headers, session.getAccessJwt());
            setRefreshJtiCookie(headers, session.getRefreshJti());
            deleteEmailConfirmationCodeIdCookie(headers);
            status = HttpStatus.OK;

        } catch (HttpException e) {
            throw e;

        } catch (RuntimeException e) {
            if (!(e instanceof AuthException)) {
                log.error(e.getMessage(), e);
            }
            deleteAllCookies(headers);
        }

        return new ResponseEntity<>(headers, status);
    }

    // success | fail -> delete current-user, ecc-id access-jwt, refresh-jti, logout-jti
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = LOGOUT_JTI_COOKIE, required = false) UUID logoutJti) {
        HttpHeaders headers = new HttpHeaders();

        try {
            service.logout(logoutJti);

        } catch (HttpException e) {
            throw e;

        } catch (RuntimeException e) {
            if (!(e instanceof AuthException)) {
                log.error(e.getMessage(), e);
            }

        } finally {
            deleteAllCookies(headers);
        }

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    void setCurrentUserCookie(HttpHeaders headers, UserModel userModel) {
        ResponseCookie cookie = ResponseCookie.from(CURRENT_USER_COOKIE, objToBase64Json(userModel))
                .httpOnly(false)
                .secure(securityProps.isCookiesAttrSecure())
                .sameSite(securityProps.getCookiesAttrSameSite())
                .path("/")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    void deleteCurrentUserCookie(HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from(CURRENT_USER_COOKIE, null)
                .path("/")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    void setEmailConfirmationCodeIdCookie(HttpHeaders headers, UUID emailConfirmationCodeId) {
        ResponseCookie cookie = ResponseCookie.from(EMAIL_CONFIRMATION_CODE_ID_COOKIE, emailConfirmationCodeId.toString())
                .httpOnly(true)
                .secure(securityProps.isCookiesAttrSecure())
                .sameSite(securityProps.getCookiesAttrSameSite())
                .path("/users/confirm")
                .maxAge(securityProps.getEmailConfirmationCodeValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    void deleteEmailConfirmationCodeIdCookie(HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from(EMAIL_CONFIRMATION_CODE_ID_COOKIE, null)
                .path("/users/confirm")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    void setAccessJwtCookie(HttpHeaders headers, String value) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_JWT_COOKIE, value)
                .httpOnly(true)
                .secure(securityProps.isCookiesAttrSecure())
                .sameSite(securityProps.getCookiesAttrSameSite())
                .path("/")
                .maxAge(securityProps.getAccessTokenValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    void deleteAccessJwtCookie(HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_JWT_COOKIE, null)
                .path("/")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    void setRefreshJtiCookie(HttpHeaders headers, UUID jti) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_JTI_COOKIE, jti.toString())
                .httpOnly(true)
                .secure(securityProps.isCookiesAttrSecure())
                .sameSite(securityProps.getCookiesAttrSameSite())
                .path("/users/refresh")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        ResponseCookie logoutCookie = ResponseCookie.from(LOGOUT_JTI_COOKIE, jti.toString())
                .httpOnly(true)
                .secure(securityProps.isCookiesAttrSecure())
                .sameSite(securityProps.getCookiesAttrSameSite())
                .path("/users/logout")
                .maxAge(securityProps.getRefreshTokenValidity())
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, logoutCookie.toString());
    }

    void deleteRefreshJtiCookie(HttpHeaders headers) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_JTI_COOKIE, null)
                .path("/users/refresh")
                .maxAge(0)
                .build();
        ResponseCookie logoutCookie = ResponseCookie.from(LOGOUT_JTI_COOKIE, null)
                .path("/users/logout")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, logoutCookie.toString());
    }

    void deleteAllCookies(HttpHeaders headers) {
        deleteCurrentUserCookie(headers);
        deleteEmailConfirmationCodeIdCookie(headers);
        deleteAccessJwtCookie(headers);
        deleteRefreshJtiCookie(headers);
    }

}
