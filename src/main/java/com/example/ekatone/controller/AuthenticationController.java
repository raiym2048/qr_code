package com.example.ekatone.controller;

import com.example.ekatone.dto.RegisterRequest;
import com.example.ekatone.dto.authenticate.AuthenticationRequest;
import com.example.ekatone.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("called");
        return service.register(request);
    }
    @PostMapping("/checkCode")
    public Boolean checkTheCode(@RequestParam(required = false) String code){
        return service.checkCode(code);
    }
    @PostMapping("/sendCode")
    public void sendCode(@RequestParam(required = false) String email){
        service.sendCode(email, service.createRandom());
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
