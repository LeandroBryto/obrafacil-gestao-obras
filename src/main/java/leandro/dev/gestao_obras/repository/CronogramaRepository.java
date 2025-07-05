package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.Cronograma;
import leandro.dev.gestao_obras.model.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CronogramaRepository extends JpaRepository<Cronograma , Long> {
    Optional<Cronograma> findByObraId(Long obraId);
    List<Cronograma> findByStatus(String status);
}
