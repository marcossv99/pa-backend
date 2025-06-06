package domain.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HorarioRequestDto {
    private LocalDate data;
    private float horaInicio;
    private float horaFim;
    private Long quadraId;
}

