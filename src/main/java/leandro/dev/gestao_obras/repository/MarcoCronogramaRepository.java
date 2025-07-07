package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.MarcoCronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcoCronogramaRepository extends JpaRepository<MarcoCronograma , Long> {
    // Busca o cronograma associado a uma obra especifica
    List<MarcoCronograma> findByCronogramaId(Long cronogramaId);
}
