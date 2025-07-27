package com.example.backclub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.backclub.dto.request.ReservaRequestDto;
import com.example.backclub.dto.response.ReservaResponseDto;
import com.example.backclub.domain.entity.Reserva;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    // Método simplificado para quando não precisamos das entidades relacionadas
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "quadra", ignore = true)
    @Mapping(target = "horario", ignore = true)
    Reserva toEntity(ReservaRequestDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nome", target = "usuarioNome")
    @Mapping(source = "usuario.fotoPerfil", target = "usuarioFotoPerfil") // Adicionar foto do usuário
    @Mapping(source = "quadra.id",  target = "quadraId")
    @Mapping(target = "quadraNome", expression = "java(\"Quadra \" + entity.getQuadra().getNumero() + \" - \" + entity.getQuadra().getModalidade())")
    @Mapping(source = "horario.id", target = "horarioId")
    @Mapping(source = "horario.data", target = "data")
    @Mapping(source = "horario.horaInicio", target = "horaInicio")
    @Mapping(source = "horario.horaFim", target = "horaFim")
    @Mapping(source = "cancelada", target = "cancelada")
    @Mapping(source = "motivoCancelamento", target = "motivoCancelamento")
    @Mapping(source = "canceladaPor", target = "canceladaPor")
    @Mapping(source = "dataCancelamento", target = "dataCancelamento")
    ReservaResponseDto toResponse(Reserva entity);
}
