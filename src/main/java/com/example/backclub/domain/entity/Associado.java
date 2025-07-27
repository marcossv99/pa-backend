package com.example.backclub.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Associado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private String telefone;
    
    // Campo original para determinar se é admin
    @Column(name = "is_admin")
    @JsonProperty("isAdmin")
    private boolean isAdmin = false;
    
    // Campo para foto de perfil
    private String fotoPerfil;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reserva> reservas;
    
    // Métodos auxiliares
    public boolean isAssociado() {
        return !this.isAdmin;
    }
    
    public String getTipo() {
        return this.isAdmin ? "ADMIN" : "ASSOCIADO";
    }
}