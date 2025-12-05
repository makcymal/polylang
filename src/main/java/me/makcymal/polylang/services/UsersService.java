package me.makcymal.polylang.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.makcymal.polylang.repositories.UsersRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository repo;


}
