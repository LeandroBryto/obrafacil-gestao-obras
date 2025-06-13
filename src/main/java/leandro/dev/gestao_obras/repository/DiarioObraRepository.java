package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.enums.TipoRegistroDiario;
import leandro.dev.gestao_obras.model.DiarioObra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiarioObraRepository extends JpaRepository<DiarioObra, Long> , JpaSpecificationExecutor<DiarioObra> {
    // Busca todos os registros de diario para uma obra específica, ordenados pr data/hora descendente
    List<DiarioObra> findObraIdOrderByDataHoraDesc(Long obraId);

    // exemplo de métodos para filtros especificos alternativa as Specifications
    List<DiarioObra> findByObraIdAndDataHoraBetweenOrderByDataHoraDesc(Long obraId, LocalDateTime inicio, LocalDateTime fim);
    List<DiarioObra> findByObraIdAndTipoOrderByDataHoraDesc(Long obraId, TipoRegistroDiario tipo);
    List<DiarioObra> findByObraIdAndEtapaRelacionadaIdOrderByDataHoraDesc(Long obraId, Long etapaId);

}
