package com.example.backclub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;
    private float horaInicio;
    private float horaFim;

    @ManyToOne
    @JoinColumn(name = "quadra_id")
    private Quadra quadra;
}
