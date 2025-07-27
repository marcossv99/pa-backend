package com.example.backclub.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDisponivelDto {
    private int horaInicio;
    private int horaFim;
    private LocalDate data;
    private boolean disponivel;
    private String status;
    
    public String getHorarioFormatado() {
        return String.format("%02d:00 - %02d:00", horaInicio, horaFim);
    }
}
