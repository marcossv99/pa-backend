package domain.mapper;

import domain.dto.request.ReservaRequestDto;
import domain.dto.response.ReservaResponseDto;
import domain.entity.Associado;
import domain.entity.Horario;
import domain.entity.Quadra;
import domain.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    /**
     * Constrói um objeto Reserva completo a partir do DTO e das entidades relacionadas injetadas:
     * - dto: contém somente os IDs (usuarioId, quadraId, horarioId) e lista de membros.
     * - usuario: objeto Associado já carregado do banco.
     * - quadra: objeto Quadra já carregado do banco.
     * - horario: objeto Horario já carregado do banco.
     */
    @Mapping(source = "dto.membros", target = "membros")
    @Mapping(source = "usuario", target = "usuario")
    @Mapping(source = "quadra",  target = "quadra")
    @Mapping(source = "horario", target = "horario")
    Reserva toEntity(ReservaRequestDto dto, Associado usuario, Quadra quadra, Horario horario);

    /**
     * Converte uma entidade Reserva em seu DTO de resposta,
     * expondo apenas os IDs de usuário, quadra e horário.
     */
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "quadra.id",  target = "quadraId")
    @Mapping(source = "horario.id", target = "horarioId")
    ReservaResponseDto toResponse(Reserva entity);
}
