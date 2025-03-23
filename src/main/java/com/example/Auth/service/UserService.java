package com.example.Auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Auth.model.User;
import com.example.Auth.respository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;

    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }


    public Optional<User> getUserByName(String name) {
        return userRepository.findByUsername(name);
    }
}