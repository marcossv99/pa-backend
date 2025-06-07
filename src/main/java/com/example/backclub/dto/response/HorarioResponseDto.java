package com.example.backclub.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HorarioResponseDto {
    private Long id;
    private LocalDate data;
    private float horaInicio;
    private float horaFim;
    private Long quadraId;
}