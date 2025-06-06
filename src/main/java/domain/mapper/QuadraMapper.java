package domain.mapper;

import domain.dto.request.QuadraRequestDto;
import domain.dto.response.QuadraResponseDto;
import domain.entity.Quadra;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface QuadraMapper {

    Quadra toEntity(QuadraRequestDto dto);

    QuadraResponseDto toResponse(Quadra entity);

    void updateEntityFromDto(QuadraRequestDto dto, @MappingTarget Quadra entity);
}
