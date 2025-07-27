package com.example.backclub.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long id;
    private String usuarioNome;
    private Long usuarioId;
    private String quadraNome;
    private Long quadraId;
    private String quadraModalidade;
    private LocalDate data;
    private Float horaInicio;
    private Float horaFim;
    private String status;
    private List<String> membros;
}
