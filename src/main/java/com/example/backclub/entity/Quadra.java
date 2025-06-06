package com.example.backclub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quadra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;
    private String modalidade;
    private int qtdPessoas;
    private String img;
    private boolean isDisponivel;

    @OneToMany(mappedBy = "quadra", cascade = CascadeType.ALL)
    private List<Horario> horarios;
}