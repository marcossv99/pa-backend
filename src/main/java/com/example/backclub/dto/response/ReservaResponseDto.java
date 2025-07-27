package com.example.backclub.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservaResponseDto {
    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioFotoPerfil; // Adicionar foto do usuário
    private Long quadraId;
    private String quadraNome;
    private Long horarioId;
    private LocalDate data;
    private float horaInicio;
    private float horaFim;
    private List<String> membros;
    
    // Campos para cancelamento
    private Boolean cancelada;
    private String motivoCancelamento;
    private String canceladaPor;
    private LocalDateTime dataCancelamento;
}
