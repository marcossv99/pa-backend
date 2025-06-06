package com.example.backclub.domain.controller;

import com.example.backclub.domain.dto.request.QuadraRequestDto;
import com.example.backclub.domain.dto.response.QuadraResponseDto;
import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.domain.mapper.QuadraMapper;
import com.example.backclub.domain.service.QuadraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quadras")
public class QuadraController {

    private final QuadraService quadraService;
    private final QuadraMapper quadraMapper;

    public QuadraController(QuadraService quadraService, QuadraMapper quadraMapper) {
        this.quadraService = quadraService;
        this.quadraMapper = quadraMapper;
    }

    @PostMapping
    public QuadraResponseDto cadastrar(@RequestBody QuadraRequestDto dto) {
        Quadra quadra = quadraService.cadastrar(quadraMapper.toEntity(dto));
        return quadraMapper.toResponse(quadra);
    }

    @GetMapping
    public List<QuadraResponseDto> listar() {
        return quadraService.listar().stream()
                .map(quadraMapper::toResponse)
                .collect(Collectors.toList());
    }
}