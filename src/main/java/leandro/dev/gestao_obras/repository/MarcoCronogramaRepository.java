package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.Cronograma;
import leandro.dev.gestao_obras.model.MarcoCronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarcoCronogramaRepository extends JpaRepository<MarcoCronograma , Long> {
    // Busca o cronograma associado a uma obra especifica
    Optional<MarcoCronograma> findByCronogramaId(Long cronogramaId);
}
