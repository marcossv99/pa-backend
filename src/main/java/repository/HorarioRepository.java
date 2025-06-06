package repository;

import domain.entity.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
}