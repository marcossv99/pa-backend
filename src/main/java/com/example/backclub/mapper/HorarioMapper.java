package com.example.backclub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.backclub.dto.request.HorarioRequestDto;
import com.example.backclub.dto.response.HorarioResponseDto;
import com.example.backclub.entity.Horario;

@Mapper(componentModel = "spring")
public interface HorarioMapper {

    Horario toEntity(HorarioRequestDto dto);

    HorarioResponseDto toResponse(Horario entity);

    void updateEntityFromDto(HorarioRequestDto dto, @MappingTarget Horario entity);
}
