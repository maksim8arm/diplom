package com.example.diplom.dto;

import org.springframework.stereotype.Component;

@Component
public class LoginPass {

    private String login;
    private String password;

    public LoginPass(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public LoginPass() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginPass{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
