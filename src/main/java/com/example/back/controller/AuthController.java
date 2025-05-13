package com.example.back.controller;

import com.example.back.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO user) {
        // Simulação de login com dados fixos
        if (
            ("admin@email.com".equals(user.getCpfOuEmail()) || "12345678900".equals(user.getCpfOuEmail()))
            && "1234".equals(user.getSenha())
        ) {
            return ResponseEntity.ok("Login realizado com sucesso!");
        } else {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }
}