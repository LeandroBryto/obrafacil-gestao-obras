package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.dto.GanttTaskDTO;
import leandro.dev.gestao_obras.enums.StatusCronograma;
import leandro.dev.gestao_obras.enums.StatusEtapa;
import leandro.dev.gestao_obras.model.Cronograma;
import leandro.dev.gestao_obras.model.Etapa;
import leandro.dev.gestao_obras.model.Obra;
import leandro.dev.gestao_obras.repository.CronogramaRepository;
import leandro.dev.gestao_obras.repository.EtapaRepository;
import leandro.dev.gestao_obras.repository.ObraRepository;
import org.aspectj.weaver.patterns.IfPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CronogramaController {

    @Autowired
    private CronogramaRepository cronogramaRepository;
    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private EtapaRepository etapaRepository;

    private static final DateTimeFormatter GANTT_DATE_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-DD");

    // Endpoint para criar um cronograma inicial para uma obra
    @PostMapping("/obras/{obraId}/cronograma")
    public ResponseEntity<Cronograma> criarCronograma(@PathVariable Long obraId, @RequestBody Cronograma cronograma){
        Optional<Obra> obraDate = obraRepository.findById(obraId);
        if (obraDate.isEmpty() || obraDate.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (cronogramaRepository.findByObraId(obraId).isPresent()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        try {
            Obra obra = obraDate.get();
            cronograma.setObra(obra);
            if (cronograma.getDataInicioProjeto() == null){
                cronograma.setDataInicioProjeto(obra.getDataInicio() !=null ? obra.getDataInicio() : LocalDate.now());
            }
            if (cronograma.getDataTerminoPrevista() == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            cronograma.setDataTerminoAtuL(cronograma.getDataTerminoPrevista());
            cronograma.setStatusGeral(StatusCronograma.NO_PRAZO);
            cronograma.setDiasAtrasoAdiantamento(0);

            Cronograma novoCronograma = cronogramaRepository.save(cronograma);
            return new ResponseEntity<>(novoCronograma, HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint para buscar o Cronograma de uma Obra
    @GetMapping("/obras/{obraId}/cronograma")
    public ResponseEntity<Cronograma> buscarCronogramaPorObra(@PathVariable Long obraId){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Cronograma> cronogramaData = cronogramaRepository.findByObraId(obraId);
        return cronogramaData.map(cronograma -> new ResponseEntity<>(cronograma,HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    // Endpoint para Atualizar informações do cronograma ( datas prevista , marcos)
    @PutMapping("/cronogramas/{cronogramaId}")
    public ResponseEntity<Cronograma> atualiarCronograma(@PathVariable Long cronogramaId , @RequestBody Cronograma cronogramaAtualizado){
        Optional<Cronograma> cronogramaData = cronogramaRepository.findById(cronogramaId);
        if (cronogramaData.isEmpty() || cronogramaData.get().getObra().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cronograma cronogramaExistente = cronogramaData.get();
        if (cronogramaAtualizado.getDataInicioProjeto() != null){
            cronogramaExistente.setDataInicioProjeto(cronogramaAtualizado.getDataInicioProjeto());
        }
        if (cronogramaAtualizado.getDataTerminoPrevista() != null){
            cronogramaExistente.setDataTerminoPrevista(cronogramaAtualizado.getDataTerminoPrevista());
            if (cronogramaExistente.getDataTerminoAtuL() == null || cronogramaExistente.getDataTerminoAtuL().isBefore(cronogramaAtualizado.getDataTerminoPrevista())){
                cronogramaExistente.setDataTerminoAtuL(cronogramaAtualizado.getDataTerminoPrevista());
            }
        }
        if (cronogramaAtualizado.getMarcosImportantes() != null){
            cronogramaExistente.setMarcosImportantes(cronogramaAtualizado.getMarcosImportantes());
        }
        recalularStatusCronograma(cronogramaExistente);

        return new ResponseEntity<>(cronogramaRepository.save(cronogramaExistente),HttpStatus.OK);
    }
    // Método auxiliar para recalcular status e datas
    private void recalularStatusCronograma(Cronograma cronograma){
        Obra obra = cronograma.getObra();
        List<Etapa> etapas = etapaRepository.findByObraIdOrderByOrdemAsc(obra.getId());

        LocalDate ultimaDataEtapa = cronograma.getDataTerminoPrevista();
        boolean algumaEtapaAtrasada = false;

        for (Etapa etapa : etapas){
            if (etapa.getStatus() != StatusEtapa.CONCLUIDO){
                LocalDate dataFimEtapa = etapa.getDataPrevistaTermino() != null ? etapa.getDataPrevistaTermino() : cronograma.getDataTerminoPrevista();
                if (etapa.getDataPrevistaTermino() != null && etapa.getDataPrevistaTermino().isBefore(LocalDate.now())){
                    etapa.setStatus(StatusEtapa.ATRASADA); // MARCA ETAPA COMO ATRASADA
                    etapaRepository.save(etapa); // Salva a alteração de status da etapa
                    algumaEtapaAtrasada = true;
                }
                if (dataFimEtapa.isEqual(ultimaDataEtapa)){
                    ultimaDataEtapa = dataFimEtapa;
                }
            }
        }
        cronograma.setDataTerminoAtuL(ultimaDataEtapa);
        long dias = ChronoUnit.DAYS.between(cronograma.getDataTerminoAtuL(), cronograma.getDataTerminoPrevista());
        cronograma.setDiasAtrasoAdiantamento((int)dias);
        if (dias > 0 && !algumaEtapaAtrasada){
            cronograma.setStatusGeral(StatusCronograma.ADIANTADA);
        } else if (dias < 0 || algumaEtapaAtrasada) {
            cronograma.setStatusGeral(StatusCronograma.ATRASADA);

        }else {
            cronograma.setStatusGeral(StatusCronograma.NO_PRAZO);
        }
    }
    @GetMapping("/cronograma/{cronograma}/relatorio")
    public ResponseEntity<Map<String,Object>> gerarRelarioCronograma(@PathVariable Long cronogramaId){
        Optional<Cronograma> cronogramaData = cronogramaRepository.findById(cronogramaId);
        if (cronogramaData.isEmpty() || cronogramaData.get().getObra().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Cronograma cronograma = cronogramaData.get();
        Obra obra = cronograma.getObra();

        Map<String, Object> relatorio = Map.of(
                "obraId", obra.getId(),
                 "obraNome",obra.getNome(),
                "cronogramaId",cronograma.getId(),
                "dataInicioProjeto",cronograma.getDataInicioProjeto(),
                "dataTerminoPrevista",cronograma.getDataTerminoPrevista(),
                "dataTerminoAtual",cronograma.getDataTerminoAtuL(),
                "statusGeral",cronograma.getStatusGeral(),
                "diasAtrasoAdiantamento",cronograma.getDiasAtrasoAdiantamento(),
                "marcosImportantes",cronograma.getMarcosImportantes()

        );
        return new ResponseEntity<>(relatorio,HttpStatus.OK);
    }
    // Endpoint Para gerar dados para o gráfico de Gantt
    @GetMapping("/obras/{obraId}/cronograma/gantt")
    public ResponseEntity<List<GanttTaskDTO>> gerarDadosGantt(@PathVariable Long obraId){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Etapa> etapas = etapaRepository.findByObraIdOrderByOrdemAsc(obraId);
        if (etapas.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<GanttTaskDTO> ganttData = new ArrayList<>();
        for (Etapa etapa : etapas){
            GanttTaskDTO task = new GanttTaskDTO();
            task.setId(etapa.getId());
            task.setText(etapa.getNome());
            LocalDate starDate = etapa.getDataPrevistaInicio() != null ? etapa.getDataPrevistaInicio() : obraData.get().getDataInicio();
            LocalDate endData = etapa.getDataPrevistaTermino();

            if (starDate != null){
                task.setStart_date(starDate.format(GANTT_DATE_FORMATTER));
            }
            if (endData != null){
                task.setEnd_date(endData.format(GANTT_DATE_FORMATTER));
            }
            if (starDate != null){
                task.setDuration((int) ChronoUnit.DAYS.between(starDate, endData) + 1);
            }
            task.setProgress(etapa.getPercentualConclusao() / 100.0); // Progresso de 0 a 1
            task.setParent(0L);
            task.setStatus(etapa.getStatus().name());
            if (etapa.getDataRealInicio() != null){
                task.setReal_start(etapa.getDataRealInicio().format(GANTT_DATE_FORMATTER));
            }
            if (etapa.getDataRealTermino() != null){
                task.setReal_end(etapa.getDataRealTermino().format(GANTT_DATE_FORMATTER));
            }

            ganttData.add(task);
        }
        return new ResponseEntity<>(ganttData, HttpStatus.OK);
    }
    

}
