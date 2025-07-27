package com.example.backclub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backclub.dto.request.AssociadoRequestDto;
import com.example.backclub.dto.response.AssociadoResponseDto;
import com.example.backclub.mapper.AssociadoMapper;
import com.example.backclub.service.AssociadoService;

import java.util.List;

@RestController
@RequestMapping("/api/associados")
@CrossOrigin(origins = "http://localhost:4200")
public class AssociadoController {
    private final AssociadoService associadoService;
    private final AssociadoMapper associadoMapper;

    public AssociadoController(AssociadoService associadoService, AssociadoMapper associadoMapper) {
        this.associadoService = associadoService;
        this.associadoMapper = associadoMapper;
    }

    @GetMapping
    public ResponseEntity<List<AssociadoResponseDto>> getAll() {
        return handle(() -> {
            List<AssociadoResponseDto> response = associadoService.findAll().stream()
                .map(associadoMapper::toResponse)
                .toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponseDto> getById(@PathVariable Long id) {
        return handle(() -> associadoService.findById(id)
            .map(associadoMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return handle(() -> {
            associadoService.delete(id);
            return ResponseEntity.noContent().build();
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociadoResponseDto> update(@PathVariable Long id, @RequestBody AssociadoRequestDto dto) {
        return handle(() -> associadoService.findById(id)
            .map(existing -> {
                associadoMapper.updateEntityFromDto(dto, existing);
                return associadoService.save(existing);
            })
            .map(associadoMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build())
        );
    }

    @PostMapping
    public ResponseEntity<AssociadoResponseDto> create(@RequestBody AssociadoRequestDto dto) {
        return handle(() -> {
            var associado = associadoMapper.toEntity(dto);
            var saved = associadoService.save(associado);
            return ResponseEntity.ok(associadoMapper.toResponse(saved));
        });
    }

    private <T> ResponseEntity<T> handle(ControllerAction<T> action) {
        try {
            return action.execute();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    private interface ControllerAction<T> {
        ResponseEntity<T> execute();
    }
}
