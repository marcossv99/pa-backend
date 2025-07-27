package com.example.backclub.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReservaRequestDto {
    private Long usuarioId; // Opcional - se não fornecido, usa usuário padrão
    private Long quadraId; // Obrigatório
    private Long horarioId; // Opcional - se não fornecido, cria novo horário
    private List<String> membros; // Opcional
    private LocalDate data; // Obrigatório
    private float horaInicio; // Obrigatório
    private float horaFim; // Obrigatório
}