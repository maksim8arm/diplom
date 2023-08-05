package com.example.diplom.service;

import com.example.diplom.dto.LoginPass;
import com.example.diplom.model.Users;
import com.example.diplom.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Transactional
@Service
public class UserService {

    private final UsersRepository usersRepository;

    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public ResponseEntity<?> checkLoginUser(@RequestBody LoginPass loginPass) {
        Users user = usersRepository.findByEmailAndPassword(loginPass.getLogin(), loginPass.getPassword());

        if (user != null) {
            Map<String, String> map = new LinkedHashMap<>();
            String token = user.getEmail() + "_" + new Date();
            user.setToken(token);
            map.put("auth-token", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            Map<String, String> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Bad Request");
            errorAnswer.put("id", "0");
            return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
        }
    }
}


