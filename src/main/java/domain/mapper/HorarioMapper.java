package domain.mapper;

import domain.dto.request.HorarioRequestDto;
import domain.dto.response.HorarioResponseDto;
import domain.entity.Horario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HorarioMapper {

    Horario toEntity(HorarioRequestDto dto);

    HorarioResponseDto toResponse(Horario entity);

    void updateEntityFromDto(HorarioRequestDto dto, @MappingTarget Horario entity);
}
