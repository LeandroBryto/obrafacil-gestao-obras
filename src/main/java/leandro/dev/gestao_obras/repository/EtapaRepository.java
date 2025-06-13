package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtapaRepository extends JpaRepository<Etapa, Long> {

    List<Etapa>findByObraIdOrdermAsc(Long obraId);
}
