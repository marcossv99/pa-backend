package com.example.backclub.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private boolean isDisponivel = true;

    @OneToMany(mappedBy = "quadra", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Horario> horarios;
}