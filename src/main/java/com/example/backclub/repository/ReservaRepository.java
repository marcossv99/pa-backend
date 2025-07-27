package com.example.backclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backclub.domain.entity.Reserva;
import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Todas as reservas do usuário (incluindo canceladas para histórico)
    List<Reserva> findByUsuarioId(Long usuarioId);

    // Reservas ativas (não canceladas) para validações
    @Query("SELECT r FROM Reserva r WHERE r.cancelada = false OR r.cancelada IS NULL")
    List<Reserva> findAllAtivas();

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "JOIN r.horario h WHERE r.quadra.id = :quadraId AND h.data >= :data " +
           "AND (r.cancelada = false OR r.cancelada IS NULL)")
    boolean existsByQuadraIdAndDataGreaterThanEqual(@Param("quadraId") Long quadraId, @Param("data") LocalDate data);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "JOIN r.horario h WHERE r.quadra.id = :quadraId AND h.data = :data " +
           "AND NOT (h.horaFim <= :horaInicio OR h.horaInicio >= :horaFim) " +
           "AND (r.cancelada = false OR r.cancelada IS NULL)")
    boolean existsByQuadraIdAndDataAndHorario(
        @Param("quadraId") Long quadraId, 
        @Param("data") LocalDate data, 
        @Param("horaInicio") float horaInicio, 
        @Param("horaFim") float horaFim
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "JOIN r.horario h WHERE r.usuario.id = :usuarioId AND h.data = :data " +
           "AND r.quadra.modalidade = :modalidade " +
           "AND (r.cancelada = false OR r.cancelada IS NULL)")
    boolean existsByUsuarioIdAndDataAndModalidade(
        @Param("usuarioId") Long usuarioId,
        @Param("data") LocalDate data,
        @Param("modalidade") String modalidade
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "JOIN r.horario h WHERE r.usuario.id = :usuarioId AND h.data = :data " +
           "AND NOT (h.horaFim <= :horaInicio OR h.horaInicio >= :horaFim) " +
           "AND (r.cancelada = false OR r.cancelada IS NULL)")
    boolean existsByUsuarioIdAndDataAndHorario(
        @Param("usuarioId") Long usuarioId,
        @Param("data") LocalDate data,
        @Param("horaInicio") float horaInicio,
        @Param("horaFim") float horaFim
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "JOIN r.horario h WHERE r.usuario.id = :usuarioId AND h.data = :data " +
           "AND (r.cancelada = false OR r.cancelada IS NULL)")
    boolean existsByUsuarioIdAndData(
        @Param("usuarioId") Long usuarioId,
        @Param("data") LocalDate data
    );

    @Query("SELECT r FROM Reserva r JOIN r.horario h WHERE r.usuario.id = :usuarioId AND h.data = :data")
    List<Reserva> findByUsuarioIdAndData(@Param("usuarioId") Long usuarioId, @Param("data") LocalDate data);

}