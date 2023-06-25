package com.example.diplom;

import com.example.diplom.model.Users;
import com.example.diplom.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DiplomApplication implements CommandLineRunner {


    UsersRepository usersRepository;

    @Autowired
    public DiplomApplication(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiplomApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {



        Users tom = new Users( "mail", "123", null);
        Users gnom = new Users( "mail2", "321", null);

        List<Users> users = Arrays.asList(tom, gnom);
        usersRepository.saveAll(users);

    }
}
