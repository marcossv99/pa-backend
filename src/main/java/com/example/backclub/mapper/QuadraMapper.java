package com.example.backclub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.backclub.dto.request.QuadraRequestDto;
import com.example.backclub.dto.response.QuadraResponseDto;
import com.example.backclub.domain.entity.Quadra;

@Mapper(componentModel = "spring")
public interface QuadraMapper {

    // Converte DTO para Entity (ignorando campos que não existem no DTO)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponivel", constant = "true")
    @Mapping(target = "horarios", ignore = true)
    Quadra toEntity(QuadraRequestDto dto);

    // Converte Entity para Response DTO
    QuadraResponseDto toResponse(Quadra entity);

    // Atualiza Entity a partir do DTO (ignorando campos que não devem ser atualizados)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponivel", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    void updateEntityFromDto(QuadraRequestDto dto, @MappingTarget Quadra entity);
}
