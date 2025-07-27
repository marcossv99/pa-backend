package com.example.backclub.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

    
import com.example.backclub.domain.entity.Associado;
import com.example.backclub.repository.AssociadoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AssociadoService {
    @Autowired
    private AssociadoRepository associadoRepository;

    public List<Associado> findAll() {
        return associadoRepository.findAll();
    }

    public Optional<Associado> findById(Long id) {
        return associadoRepository.findById(id);
    }

    public Associado save(Associado associado) {
        return associadoRepository.save(associado);
    }

    public void delete(Long id) {
        associadoRepository.deleteById(id);
    }

    public Optional<Associado> findByCpfOrEmail(String login) {
        return associadoRepository.findByCpfOrEmail(login);
    }
    
    public Optional<Associado> findByCpf(String cpf) {
        return associadoRepository.findByCpf(cpf);
    }
    
    public Optional<Associado> findByEmail(String email) {
        return associadoRepository.findByEmail(email);
    }

    public Associado buscarPorId(Long id) {
        return associadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));
    }
}
