package com.example.backclub.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.backclub.domain.dto.request.QuadraRequestDto;
import com.example.backclub.domain.dto.response.QuadraResponseDto;
import com.example.backclub.domain.entity.Quadra;

@Mapper(componentModel = "spring")
public interface QuadraMapper {

    Quadra toEntity(QuadraRequestDto dto);

    QuadraResponseDto toResponse(Quadra entity);

    void updateEntityFromDto(QuadraRequestDto dto, @MappingTarget Quadra entity);
}
