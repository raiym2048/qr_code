package com.example.ekatone.service;

import com.example.ekatone.dto.RegisterRequest;
import com.example.ekatone.dto.authenticate.AuthenticationRequest;
import com.example.ekatone.dto.authenticate.AuthenticationResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationService {
    ResponseEntity<?> register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    Boolean checkCode(String code);

    String createRandom();

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;

    void sendCode(String email, String randomCode);
}
