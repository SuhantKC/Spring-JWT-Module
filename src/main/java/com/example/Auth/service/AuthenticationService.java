package com.example.Auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Auth.dto.LoginUserDto;
import com.example.Auth.dto.RegisterUserDto;
import com.example.Auth.dto.VerifyUserDto;
import com.example.Auth.model.User;
import com.example.Auth.respository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public User signUpUser(RegisterUserDto inputInfo) {

        User user = new User(inputInfo.getUsername(), inputInfo.getEmail(),
                passwordEncoder.encode(inputInfo.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(true);
//        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public User authenticateUser(LoginUserDto inputInfo) {
        User user = userRepository.findByEmail(inputInfo.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid login credentials"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User is not verified");
        }

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(inputInfo.getEmail(), inputInfo.getPassword()));

        return user;
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        Optional<User> optUser = userRepository.findByEmail(verifyUserDto.getEmail());
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(user.getVerificationCode())) {
                user.setEnabled(true);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }

        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isEnabled()) {
            throw new RuntimeException("User is already verified");
        }
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    public void sendVerificationEmail(User user) {
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationCode());

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }
}