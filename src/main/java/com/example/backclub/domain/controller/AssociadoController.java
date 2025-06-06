package com.example.backclub.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backclub.domain.dto.request.AssociadoRequestDto;
import com.example.backclub.domain.dto.response.AssociadoResponseDto;
import com.example.backclub.domain.entity.Associado;
import com.example.backclub.domain.mapper.AssociadoMapper;
import com.example.backclub.domain.service.AssociadoService;

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
}
