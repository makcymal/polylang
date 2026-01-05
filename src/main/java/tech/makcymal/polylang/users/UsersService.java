package tech.makcymal.polylang.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import tech.makcymal.polylang.common.exceptions.HttpException;
import tech.makcymal.polylang.security.JwtService;
import tech.makcymal.polylang.security.SecurityProperties;
import tech.makcymal.polylang.security.exceptions.AuthException;
import tech.makcymal.polylang.users.dto.CheckIfExistsResponse;
import tech.makcymal.polylang.users.dto.Session;
import tech.makcymal.polylang.users.dto.LoginRequest;
import tech.makcymal.polylang.users.dto.RegisterRequest;
import tech.makcymal.polylang.users.email_confirmation.EmailConfirmationEntity;
import tech.makcymal.polylang.users.email_confirmation.EmailConfirmationRepo;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.users.email_confirmation.EmailConfirmationRequest;
import tech.makcymal.polylang.users.tokens.RefreshTokenEntity;
import tech.makcymal.polylang.users.tokens.RefreshTokensRepo;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private static final String EMAIL_REGEX =
            "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final SecurityProperties securityProps;
    private final JwtService jwtService;
    private final UsersRepo usersRepo;
    private final EmailConfirmationRepo emailConfirmationRepo;
    private final RefreshTokensRepo refreshTokensRepo;

    public boolean isThisEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }

    public CheckIfExistsResponse checkIfExists(String emailOrUsername) {
        if (isThisEmail(emailOrUsername)) {
            return new CheckIfExistsResponse(usersRepo.existsByEmail(emailOrUsername), true);
        } else {
            return new CheckIfExistsResponse(usersRepo.existsByUsername(emailOrUsername), false);
        }
    }

    @Transactional
    public Session register(RegisterRequest request) {
        UserEntity savedEntity;

        try {
            savedEntity = usersRepo.save(registerRequestToEntity(request));
        } catch (DataIntegrityViolationException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }

        Session session = Session.ofNulls();
        session.setCurrentUser(entityToModel(savedEntity));

        provideEmailConfirmation(session);

        return session;
    }

    @Transactional
    public Session confirmEmail(EmailConfirmationRequest request) {
        usersRepo.tryToConfirmEmail(request.getEmail(), request.getCode());

        Optional<UserEntity> userEntityOpt = usersRepo.findByEmail(request.getEmail());
        if (userEntityOpt.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "User with this email doesn't exist");
        }
        UserEntity userEntity = userEntityOpt.get();

        Session session = Session.ofNulls();
        session.setCurrentUser(entityToModel(userEntity));

        provideEmailConfirmation(session);
        generateTokens(session);

        return session;
    }

    @Transactional
    public Session provideEmailConfirmation(String email) {
        Optional<UserEntity> userEntityOpt = usersRepo.findByEmail(email);
        if (userEntityOpt.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "User with this email doesn't exist");
        }
        UserEntity userEntity = userEntityOpt.get();

        Session session = Session.ofNulls();
        session.setCurrentUser(entityToModel(userEntity));

        provideEmailConfirmation(session);

        return session;
    }

    @Transactional
    public Session login(LoginRequest request) {
        Optional<UserEntity> userEntityOpt = isThisEmail(request.getEmailOrUsername())
                                             ? usersRepo.findByEmail(request.getEmailOrUsername())
                                             : usersRepo.findByUsername(request.getEmailOrUsername());

        if (userEntityOpt.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "User with this email doesn't exist");
        }
        UserEntity userEntity = userEntityOpt.get();

        boolean pwCorrect = BCrypt.checkpw(request.getPassword(), userEntity.getPasswordHash());
        if (!pwCorrect) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }

        Session session = Session.ofNulls();
        session.setCurrentUser(entityToModel(userEntity));

        if (userEntity.getEmailConfirmed()) {
            generateTokens(session);
        } else {
            provideEmailConfirmation(session);
        }

        return session;
    }

    @Transactional
    public Session refreshTokens(UUID oldRefreshJti) {
        if (oldRefreshJti == null) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh jti is null");
        }

        Optional<RefreshTokenEntity> refreshTokenEntityOpt =
                refreshTokensRepo.findByJtiAndExpiresAtAfter(oldRefreshJti, ZonedDateTime.now());
        if (refreshTokenEntityOpt.isEmpty()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh token not found or expired");
        }
        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();

        Optional<UserEntity> userEntityOpt = usersRepo.findById(refreshTokenEntity.getUserId());
        if (userEntityOpt.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "There is no user who owns this refresh token");
        }
        UserEntity userEntity = userEntityOpt.get();

        Session session = Session.ofNulls();
        session.setCurrentUser(entityToModel(userEntity));

        generateTokens(session);

        return session;
    }

    @Transactional
    public void logout(UUID refreshJti) {
        if (refreshJti == null) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Refresh jti is null");
        }
        refreshTokensRepo.deleteByJti(refreshJti);
    }

    void provideEmailConfirmation(Session session) {
        UserModel user = session.getCurrentUser();
        if (user.isEmailConfirmed()) {
            session.setEmailConfirmationCodeId(null);
            return;
        }

        emailConfirmationRepo.deleteAllByEmailAndExpiresAtBefore(user.getEmail(), ZonedDateTime.now());
        Optional<EmailConfirmationEntity> foundEmailConfirmationCode = emailConfirmationRepo.findByEmail(user.getEmail());

        if (foundEmailConfirmationCode.isPresent()) {
            session.setEmailConfirmationCodeId(foundEmailConfirmationCode.get().getId());
        } else {
            UUID id = UUID.randomUUID();
            EmailConfirmationEntity emailConfirmationEntity = EmailConfirmationEntity.builder()
                    .id(id)
                    .email(user.getEmail())
                    .code(String.valueOf(ThreadLocalRandom.current().nextInt((int) 1e+5, (int) 1e+6)))
                    .expiresAt(ZonedDateTime.now().plus(securityProps.getEmailConfirmationCodeValidity()))
                    .build();
            emailConfirmationRepo.save(emailConfirmationEntity);

            log.info("Confirm - email: [{}], code: [{}]", user.getEmail(), emailConfirmationEntity.getCode());

            session.setEmailConfirmationCodeId(id);
        }
    }

    void generateTokens(Session session) {
        UserModel user = session.getCurrentUser();

        UUID refreshJti = UUID.randomUUID();
        UUID accessJti = UUID.randomUUID();
        issueRefreshJwt(user.getId(), accessJti, refreshJti);
        String accessJwt = issueAccessJwt(accessJti, user.getId());

        session.setAccessJwt(accessJwt);
        session.setRefreshJti(refreshJti);
    }

    void issueRefreshJwt(UUID userId, UUID accessJti, UUID refreshJti) {
        refreshTokensRepo.deleteAllByUserId(userId);

        ZonedDateTime now = ZonedDateTime.now();
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .jti(refreshJti)
                .accessJti(accessJti)
                .userId(userId)
                .expiresAt(now.plus(securityProps.getRefreshTokenValidity()))
                .build();

        refreshTokensRepo.save(entity);
    }

    String issueAccessJwt(UUID jti, UUID userId) {
        return jwtService.issueJwt(jti, userId.toString(), securityProps.getAccessTokenValidity());
    }

    UserEntity registerRequestToEntity(RegisterRequest request) {
        String hashedPw = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        ZonedDateTime now = ZonedDateTime.now();

        return UserEntity.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .emailConfirmed(false)
                .username(request.getUsername())
                .passwordHash(hashedPw)
                .createdAt(now)
                .updatedAt(now)
                .lastAuthenticationAt(now)
                .build();
    }

    UserModel entityToModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserModel.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .emailConfirmed(entity.getEmailConfirmed())
                .username(entity.getUsername())
                .build();
    }

}
