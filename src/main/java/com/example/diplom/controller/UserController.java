package com.example.diplom.controller;

import com.example.diplom.dto.LoginPass;
import com.example.diplom.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Transactional
@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginPass loginPass) {
        return userService.checkLoginUser(loginPass);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> loginOut(@RequestHeader("auth-token") String token) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
