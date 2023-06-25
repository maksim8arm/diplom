package com.example.diplom.repository;

import com.example.diplom.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    Users findByEmailAndPassword(String email, String password);

    @Query(value = "SELECT * FROM users WHERE id = ?1", nativeQuery = true)
    Users findId(Long token);

    @Query(value = "SELECT * FROM users WHERE token = ?1", nativeQuery = true)
    Optional<Users> findUserByToken(String token);

    @Query(value = "SELECT * FROM users WHERE id = ?1", nativeQuery = true)
    Optional<Users> findUserId(Long id);

    @NonNull
    List<Users> findAll();


}
