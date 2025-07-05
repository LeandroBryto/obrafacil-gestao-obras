package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.enums.StatusCronograma;
import leandro.dev.gestao_obras.model.Cronograma;
import leandro.dev.gestao_obras.model.Obra;
import leandro.dev.gestao_obras.repository.CronogramaRepository;
import leandro.dev.gestao_obras.repository.EtapaRepository;
import leandro.dev.gestao_obras.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
}
