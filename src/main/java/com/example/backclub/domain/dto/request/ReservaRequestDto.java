package com.example.backclub.domain.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ReservaRequestDto {
    private Long usuarioId;
    private Long quadraId;
    private Long horarioId;
    private List<String> membros;
}