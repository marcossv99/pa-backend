package com.example.back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.SocioCadastroDTO;

@RestController 
@RequestMapping("/api/socios") // Mapeia a URL base para este controlador
public class SocioController {

    // Endpoint para cadastrar um novo associado
    // O método recebe um objeto SocioCadastroDTO no corpo da requisição
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrarSocio(@RequestBody SocioCadastroDTO socioCadastroDTO) {
        // Aqui você pode implementar a lógica de cadastro, como salvar os dados em um
        // banco de dados
        System.out.println("Cadastro de associado" + socioCadastroDTO);
        return ResponseEntity.ok("Associado realizado com sucesso!");
    }
}
