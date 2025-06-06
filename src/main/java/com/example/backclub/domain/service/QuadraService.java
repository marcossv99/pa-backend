package com.example.backclub.domain.service;

import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.repository.QuadraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuadraService {

    private final QuadraRepository quadraRepository;

    public QuadraService(QuadraRepository quadraRepository) {
        this.quadraRepository = quadraRepository;
    }

    public Quadra cadastrar(Quadra quadra) {
        return quadraRepository.save(quadra);
    }

    public List<Quadra> listar() {
        return quadraRepository.findAll();
    }
    public Quadra buscarPorId(Long id) {
    return quadraRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Quadra não encontrada"));
}
}