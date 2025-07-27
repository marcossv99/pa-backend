package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backclub.domain.entity.Associado;

import java.util.List;
import java.util.Optional;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
    Optional<Associado> findByCpf(String cpf);
    Optional<Associado> findByEmail(String email);
    Optional<Associado> findByTelefone(String telefone);
    
    // Buscar por admin status
    List<Associado> findByIsAdmin(boolean isAdmin);
    
    // Verificar se é admin
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Associado a WHERE a.id = :id AND a.isAdmin = true")
    boolean isAdmin(@Param("id") Long id);
    
    // Busca por cpf ou email (método auxiliar)
    default Optional<Associado> findByCpfOrEmail(String login) {
        Optional<Associado> associado = findByCpf(login);
        if (associado.isEmpty()) {
            associado = findByEmail(login);
        }
        return associado;
    }
}