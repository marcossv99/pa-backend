package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backclub.domain.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}