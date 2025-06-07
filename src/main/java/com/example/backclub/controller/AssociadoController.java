package com.example.backclub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backclub.dto.request.AssociadoRequestDto;
import com.example.backclub.dto.response.AssociadoResponseDto;
import com.example.backclub.domain.entity.Associado;
import com.example.backclub.mapper.AssociadoMapper;
import com.example.backclub.service.AssociadoService;

import java.util.List;

@RestController
@RequestMapping("/api/associados")
public class AssociadoController {
    @Autowired
    private AssociadoService associadoService;

    @Autowired
    private AssociadoMapper associadoMapper;

    @GetMapping
    public List<AssociadoResponseDto> getAll() {
        return associadoService.findAll().stream().map(associadoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponseDto> getById(@PathVariable Long id) {
        return associadoService.findById(id)
                .map(associadoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AssociadoResponseDto create(@RequestBody AssociadoRequestDto dto) {
        Associado associado = associadoMapper.toEntity(dto);
        Associado salvo = associadoService.save(associado);
        return associadoMapper.toResponse(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        associadoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RestController
    @RequestMapping("/api/auth")
    public static class AuthController {

        @Autowired
        private AssociadoService associadoService;

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody AssociadoRequestDto associadoRequestDto) {
            // Busca por email OU cpf
            String login = null;
            if (associadoRequestDto.getCpf() != null && !associadoRequestDto.getCpf().isEmpty()) {
                login = associadoRequestDto.getCpf();
            } else if (associadoRequestDto.getEmail() != null && !associadoRequestDto.getEmail().isEmpty()) {
                login = associadoRequestDto.getEmail();
            }
            Associado associado = null;
            if (login != null) {
                associado = associadoService.findByCpfOrEmail(login);
            }
            if (associado == null) {
                return ResponseEntity.status(401).body("Usuário não encontrado");
            }
            if (!associado.getSenha().equals(associadoRequestDto.getSenha())) {
                return ResponseEntity.status(401).body("Senha incorreta");
            }
            return ResponseEntity.ok("Login realizado com sucesso!");
        }
    }
}
