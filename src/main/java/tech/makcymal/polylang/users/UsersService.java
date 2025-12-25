package tech.makcymal.polylang.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import tech.makcymal.polylang.users.entities.EmailConfirmationCodeEntity;
import tech.makcymal.polylang.users.repos.EmailConfirmationCodesRepo;
import tech.makcymal.polylang.users.repos.UsersRepo;
import org.springframework.stereotype.Service;
import org.hibernate.exception.ConstraintViolationException;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepo usersRepo;
    private final EmailConfirmationCodesRepo emailConfirmationCodesRepo;

}
