package com.example.backclub.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE associado SET is_valido = false WHERE id = ?")
@Where(clause = "is_valido = true")
public class Associado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private String telefone;

    private boolean isAdmin;
    private boolean isValido = true;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;
}