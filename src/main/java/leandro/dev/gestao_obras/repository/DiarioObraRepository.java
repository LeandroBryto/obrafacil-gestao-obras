package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.enums.TipoRegistroDiario;
import leandro.dev.gestao_obras.model.DiarioObra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiarioObraRepository extends JpaRepository<DiarioObra, Long>, JpaSpecificationExecutor<DiarioObra> {

    // Correto: busca por obraId ordenando por dataHora decrescente
    List<DiarioObra> findByObraIdOrderByDataHoraDesc(Long obraId);

    List<DiarioObra> findByObraIdAndDataHoraBetweenOrderByDataHoraDesc(Long obraId, LocalDateTime inicio, LocalDateTime fim);
    List<DiarioObra> findByObraIdAndTipoOrderByDataHoraDesc(Long obraId, TipoRegistroDiario tipo);
    List<DiarioObra> findByObraIdAndEtapaRelacionadaIdOrderByDataHoraDesc(Long obraId, Long etapaId);
}
