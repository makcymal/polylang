package tech.makcymal.polylang.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.makcymal.polylang.users.repos.UsersRepo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepo repo;


}
