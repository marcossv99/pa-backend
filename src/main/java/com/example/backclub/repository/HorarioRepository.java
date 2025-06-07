package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backclub.domain.entity.Horario;
public interface HorarioRepository extends JpaRepository<Horario, Long> {
}