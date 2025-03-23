package com.example.Auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Auth.dto.LoginUserDto;
import com.example.Auth.dto.RegisterUserDto;
import com.example.Auth.dto.VerifyUserDto;
import com.example.Auth.model.User;
import com.example.Auth.response.LoginResponse;
import com.example.Auth.service.AuthenticationService;
import com.example.Auth.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginCredentials) {

        User user = authenticationService.authenticateUser(loginCredentials);
        String token = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse(token, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto user) {
        User registerUser = authenticationService.signUpUser(user);
        return ResponseEntity.ok(registerUser);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyUserDto userDto) {
        try {
            authenticationService.verifyUser(userDto);

            return ResponseEntity.ok("Account Verified");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestBody String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}