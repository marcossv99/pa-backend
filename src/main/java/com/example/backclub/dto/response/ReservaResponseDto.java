package com.example.backclub.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReservaResponseDto {
    private Long id;
    private Long usuarioId;
    private Long quadraId;
    private Long horarioId;
    private List<String> membros;
}
