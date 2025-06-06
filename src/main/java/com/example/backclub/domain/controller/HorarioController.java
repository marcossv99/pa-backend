package com.example.backclub.domain.controller;

import com.example.backclub.domain.dto.request.HorarioRequestDto;
import com.example.backclub.domain.dto.response.HorarioResponseDto;
import com.example.backclub.domain.entity.Horario;
import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.domain.mapper.HorarioMapper;
import com.example.backclub.domain.service.HorarioService;
import com.example.backclub.domain.service.QuadraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final HorarioMapper horarioMapper;
    private final QuadraService quadraService;

    public HorarioController(HorarioService horarioService, HorarioMapper horarioMapper, QuadraService quadraService) {
        this.horarioService = horarioService;
        this.horarioMapper = horarioMapper;
        this.quadraService = quadraService;
    }

    @PostMapping
    public HorarioResponseDto cadastrar(@RequestBody HorarioRequestDto dto) {
        Quadra quadra = quadraService.buscarPorId(dto.getQuadraId());
        Horario horario = horarioMapper.toEntity(dto);
        horario.setQuadra(quadra);
        return horarioMapper.toResponse(horarioService.cadastrar(horario));
    }

    @GetMapping
    public List<HorarioResponseDto> listar() {
        return horarioService.listar().stream()
                .map(horarioMapper::toResponse)
                .collect(Collectors.toList());
    }
}