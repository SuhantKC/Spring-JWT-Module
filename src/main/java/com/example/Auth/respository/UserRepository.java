package com.example.Auth.respository;

import org.springframework.stereotype.Repository;

import com.example.Auth.model.User;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String verificationCode);
}
