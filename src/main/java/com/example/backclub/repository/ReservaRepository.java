package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backclub.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}