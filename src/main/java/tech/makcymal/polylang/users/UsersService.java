package tech.makcymal.polylang.users;

import com.auth0.jwt.JWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import tech.makcymal.polylang.common.exceptions.HttpException;
import tech.makcymal.polylang.security.JwtService;
import tech.makcymal.polylang.security.SecurityProperties;
import tech.makcymal.polylang.users.dto.CheckIfExistsResponse;
import tech.makcymal.polylang.users.dto.Cookies;
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

    public UserModel findByEmail(String email) {
        UserEntity userEntity = usersRepo.findByEmail(email).orElse(null);
        return entityToModel(userEntity);
    }

    public UserModel register(RegisterRequest request) {
        UserEntity savedEntity;

        try {
            savedEntity = usersRepo.save(registerRequestToEntity(request));
        } catch (DataIntegrityViolationException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }

        startEmailConfirmation(savedEntity);

        return entityToModel(savedEntity);
    }

    void startEmailConfirmation(UserEntity user) {
        emailConfirmationRepo.deleteAllByEmail(user.getEmail());

        EmailConfirmationEntity emailConfirmationEntity = EmailConfirmationEntity.builder()
                .email(user.getEmail())
                .code(String.valueOf(ThreadLocalRandom.current().nextInt((int) 1e+5, (int) 1e+6)))
                .expiresAt(ZonedDateTime.now().plusDays(1))
                .build();

        emailConfirmationRepo.save(emailConfirmationEntity);

        log.info("Confirm - email: [{}], code: [{}]", user.getEmail(), emailConfirmationEntity.getCode());
    }

    public Cookies confirmEmail(EmailConfirmationRequest request) {
        usersRepo.tryToConfirmEmail(request.getEmail(), request.getCode());

        Optional<UserEntity> userEntityOpt = usersRepo.findByEmail(request.getEmail());
        if (userEntityOpt.isEmpty()) {
            return Cookies.ofNulls();
        }
        UserEntity userEntity = userEntityOpt.get();

        UUID refreshJti = UUID.randomUUID();
        UUID accessJti = UUID.randomUUID();
        issueRefreshJwt(refreshJti, accessJti, userEntity.getId());
        String accessJwt = issueAccessJwt(accessJti, userEntity.getEmail());

        return new Cookies(entityToModel(userEntity), accessJwt, refreshJti);
    }

    void issueRefreshJwt(UUID refreshJti, UUID accessJti, int userId) {
        forget(refreshJti, userId);

        ZonedDateTime now = ZonedDateTime.now();
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .jti(refreshJti)
                .accessJti(accessJti)
                .userId(userId)
                .expiresAt(now.plus(securityProps.getRefreshTokenValidity()))
                .build();

        refreshTokensRepo.save(entity);
    }

    String issueAccessJwt(UUID jti, String userEmail) {
        return jwtService.issueJwt(jti, userEmail, securityProps.getAccessTokenValidity());
    }

    public Cookies login(LoginRequest request) {
        Optional<UserEntity> userEntityOpt = isThisEmail(request.getEmailOrUsername())
                                             ? usersRepo.findByEmail(request.getEmailOrUsername())
                                             : usersRepo.findByUsername(request.getEmailOrUsername());

        if (userEntityOpt.isEmpty()) {
            return Cookies.ofNulls();
        }
        UserEntity userEntity = userEntityOpt.get();

        boolean pwCorrect = BCrypt.checkpw(request.getPassword(), userEntity.getPasswordHash());
        if (!pwCorrect) {
            return Cookies.ofNulls();
        }

        forget(userEntity.getId());
        UUID refreshJti = UUID.randomUUID();
        UUID accessJti = UUID.randomUUID();
        issueRefreshJwt(refreshJti, accessJti, userEntity.getId());
        String accessJwt = issueAccessJwt(accessJti, userEntity.getEmail());

        return new Cookies(entityToModel(userEntity), accessJwt, refreshJti);
    }

    public Cookies refreshTokens(UUID oldRefreshJti) {
        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokensRepo.findByJti(oldRefreshJti);
        if (refreshTokenEntityOpt.isEmpty() || refreshTokenEntityOpt.get().getExpiresAt().isAfter(ZonedDateTime.now())) {
            return Cookies.ofNulls();
        }
        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();

        Optional<UserEntity> userEntityOpt = usersRepo.findById(refreshTokenEntity.getUserId());
        if (userEntityOpt.isEmpty()) {
            return Cookies.ofNulls();
        }
        UserEntity userEntity = userEntityOpt.get();

        forget(oldRefreshJti, userEntity.getId());
        UUID newRefreshJti = UUID.randomUUID();
        UUID accessJti = UUID.randomUUID();
        issueRefreshJwt(newRefreshJti, accessJti, userEntity.getId());
        String accessJwt = issueAccessJwt(accessJti, userEntity.getEmail());

        return new Cookies(entityToModel(userEntity), accessJwt, newRefreshJti);
    }

    public void forget(UUID refreshJti) {
        if (refreshJti == null) {
            return;
        }
        refreshTokensRepo.deleteByJti(refreshJti);
    }

    public void forget(int userId) {
        refreshTokensRepo.deleteAllByUserId(userId);
    }

    public void forget(UUID refreshJti, int userId) {
        forget(refreshJti);
        forget(userId);
    }

    UserEntity registerRequestToEntity(RegisterRequest request) {
        String hashedPw = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        ZonedDateTime now = ZonedDateTime.now();

        return UserEntity.builder()
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
