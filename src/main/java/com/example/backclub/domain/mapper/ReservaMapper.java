package com.example.backclub.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.backclub.domain.dto.request.ReservaRequestDto;
import com.example.backclub.domain.dto.response.ReservaResponseDto;
import com.example.backclub.domain.entity.Associado;
import com.example.backclub.domain.entity.Horario;
import com.example.backclub.domain.entity.Quadra;
import com.example.backclub.domain.entity.Reserva;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    /**
     * Constrói um objeto Reserva completo a partir do DTO e das entidades relacionadas injetadas:
     * - dto: contém somente os IDs (usuarioId, quadraId, horarioId) e lista de membros.
     * - usuario: objeto Associado já carregado do banco.
     * - quadra: objeto Quadra já carregado do banco.
     * - horario: objeto Horario já carregado do banco.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.membros", target = "membros")
    @Mapping(source = "usuario", target = "usuario")
    @Mapping(source = "quadra",  target = "quadra")
    @Mapping(source = "horario", target = "horario")
    Reserva toEntity(ReservaRequestDto dto, Associado usuario, Quadra quadra, Horario horario);

    @Mapping(target = "id", source = "id")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "quadra.id",  target = "quadraId")
    @Mapping(source = "horario.id", target = "horarioId")
    ReservaResponseDto toResponse(Reserva entity);
}
