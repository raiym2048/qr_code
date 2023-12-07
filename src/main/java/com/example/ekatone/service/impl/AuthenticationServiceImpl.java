package com.example.ekatone.service.impl;

import com.example.ekatone.dto.RegisterRequest;
import com.example.ekatone.dto.authenticate.AuthenticationRequest;
import com.example.ekatone.dto.authenticate.AuthenticationResponse;
import com.example.ekatone.dto.user.UserResponse;
import com.example.ekatone.entities.User;
import com.example.ekatone.enums.Role;
import com.example.ekatone.exception.BadCredentialsException;
import com.example.ekatone.repositories.UserRepository;
import com.example.ekatone.security.JwtService;
import com.example.ekatone.security.JwtTokenProvider;
import com.example.ekatone.service.AuthenticationService;
import com.example.ekatone.service.EmailSenderService;
import com.example.ekatone.token.Token;
import com.example.ekatone.token.TokenRepository;
import com.example.ekatone.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailSenderService emailSenderService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        System.out.println("called2");

        String theRandomCode = checkUsernameIsExist(request.getEmail(), request.getNickname());
        System.out.println("called3");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setCode(theRandomCode);
        // sendCode(user.getEmail(), theRandomCode);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setNickname(request.getNickname());
        user.setRole(Role.USER);
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);


        userRepository.save(user);

        return ResponseEntity.ok(convertAuthentication(user, refreshToken));
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Optional<User> user1 = userRepository.findByEmailOrNickname(request.getEmail(), request.getEmail());
        if (user1.isEmpty())
            throw new BadCredentialsException("the nickname or email ");

        try {
            if (!user1.get().getChecked()){
                userRepository.delete(user1.get());
                throw new BadCredentialsException("Invalid username, email or password!");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user1.get().getEmail(), // Передайте значение email или никнейма
                    request.getPassword()));
        } catch (AuthenticationException e) {
            // Обработка ошибки аутентификации, например, неверный email или пароль
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
        }


        User user = userRepository.findByEmailOrNickname(request.getEmail(), request.getEmail()).orElseThrow(() -> new BadCredentialsException("User not found"));
        String token = jwtTokenProvider.createToken(user.getEmail(), userRepository.findByEmail(user.getEmail()).get().getRole());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .user(convertToresponse(user))
                .refreshToken(refreshToken)
                .accessToken(token)
                .build();
    }


    @Override
    public Boolean checkCode(String code) {
        Optional<User> user = userRepository.findByCode(code);
        if (user.isPresent()){
            user.get().setChecked(true);
            userRepository.save(user.get());
            return true;
        }
        return false;
    }
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private AuthenticationResponse convertAuthentication(User user, String refreshToken) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setUser(convertToresponse(user));
        response.setRefreshToken(refreshToken);
        String token = jwtTokenProvider.createToken(user.getEmail(), userRepository.findByEmail(user.getEmail()).get().getRole());
        response.setAccessToken(token);
        System.out.println("called8");

        return response;
    }
    private UserResponse convertToresponse(User user) {
        System.out.println("called6");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setNickname(user.getNickname());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        System.out.println("called7");

        return userResponse;
    }
    private String checkUsernameIsExist(String email, String nickname) {
        Optional<User> userEmail = userRepository.findByEmail(email);
        Optional<User> userNick = userRepository.findByNickname(nickname);
        if (userEmail.isPresent()){
            if (!userEmail.get().getChecked()) {
                userRepository.delete(userEmail.get());
                return "";
            }
            throw new BadCredentialsException("user with this email is already exists!");
        }
        if(userNick.isPresent()){
            if (!userNick.get().getChecked()) {
                userRepository.delete(userNick.get());
                return "";
            }
            throw new BadCredentialsException("user with this nickname is already exists!");
        }
        String theRandomCode = createRandom();

        // emailSenderService.sendEmail(email, "Проверка почты", "Код для подтвердения: "+ theRandomCode, Integer.parseInt(theRandomCode));
        return  theRandomCode;

    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    @Override
    public String createRandom() {
        Random random = new Random();
        int min = 100000, max = 999999;
        return String.valueOf((random.nextInt((max - min) + 1) + min));
    }
    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    @Override
    public void sendCode(String email, String randomCode) {

//        Optional<User> user = userRepository.findByEmailOrNickname(email, email);
//        user.get().setCode(randomCode);
//        userRepository.save(user.get());
//        if (user.isPresent()) {
//
//        }
//        else {
//         //   throw new BadCredentialsException("username or email не существует!");
//        }
        emailSenderService.sendEmail(email, "Проверка почты", "Код для подтвердения: "+ randomCode, Integer.parseInt(randomCode));
    }
}
