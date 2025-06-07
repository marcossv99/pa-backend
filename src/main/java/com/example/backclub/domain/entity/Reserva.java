package com.example.backclub.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Associado usuario;

    @ManyToOne
    @JoinColumn(name = "quadra_id")
    private Quadra quadra;

    @ManyToOne
    @JoinColumn(name = "horario_id")
    private Horario horario;

    @ElementCollection
    private List<String> membros;
}

