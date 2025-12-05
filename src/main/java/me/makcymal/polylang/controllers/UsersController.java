package me.makcymal.polylang.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.makcymal.polylang.dtos.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    // @PostMapping("/login")
    // @ResponseStatus(HttpStatus.OK)


    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getCurrentUser(HttpServletRequest request) {
        throw new RuntimeException("Not implemented");
    }

}
