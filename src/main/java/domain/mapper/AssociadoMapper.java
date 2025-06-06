package domain.mapper;

import domain.dto.request.AssociadoRequestDto;
import domain.dto.response.AssociadoResponseDto;
import domain.entity.Associado;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AssociadoMapper {

    Associado toEntity(AssociadoRequestDto dto);

    AssociadoResponseDto toResponse(Associado entity);

    void updateEntityFromDto(AssociadoRequestDto dto, @MappingTarget Associado entity);
}
