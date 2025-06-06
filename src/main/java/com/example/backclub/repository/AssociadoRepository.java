package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backclub.domain.entity.Associado;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
}