package com.example.backclub.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

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
    @JsonIgnoreProperties({"reservas", "password", "senha"})
    private Associado usuario;

    @ManyToOne
    @JoinColumn(name = "quadra_id")
    @JsonIgnoreProperties({"reservas"})
    private Quadra quadra;

    @ManyToOne
    @JoinColumn(name = "horario_id")
    @JsonIgnoreProperties({"reservas"})
    private Horario horario;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> membros = new ArrayList<>();

    // Campos para cancelamento (soft delete)
    @Column(name = "cancelada")
    private Boolean cancelada = false;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    @Column(name = "cancelada_por")
    private String canceladaPor; // "ADMIN" ou "ASSOCIADO"

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;
}

