package com.example.backclub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.backclub.dto.request.AssociadoRequestDto;
import com.example.backclub.dto.response.AssociadoResponseDto;
import com.example.backclub.domain.entity.Associado;
@Mapper(componentModel = "spring")
public interface AssociadoMapper {

    Associado toEntity(AssociadoRequestDto dto);

    AssociadoResponseDto toResponse(Associado entity);

    void updateEntityFromDto(AssociadoRequestDto dto, @MappingTarget Associado entity);
}
