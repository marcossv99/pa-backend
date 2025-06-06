package repository;

import domain.entity.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
}