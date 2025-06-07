package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backclub.domain.entity.Associado;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
    Associado findByCpf(String cpf);
    Associado findByEmail(String email);
    Associado findByTelefone(String telefone);

    // Busca por cpf ou email
    default Associado findByCpfOrEmail(String login) {
        Associado associado = findByCpf(login);
        if (associado == null) {
            associado = findByEmail(login);
        }
        return associado;
    }
}