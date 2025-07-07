package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.model.Cronograma;
import leandro.dev.gestao_obras.model.MarcoCronograma;
import leandro.dev.gestao_obras.repository.ClienteRepository;
import leandro.dev.gestao_obras.repository.CronogramaRepository;
import leandro.dev.gestao_obras.repository.MarcoCronogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MarcoCronogramaController {

    @Autowired
    private MarcoCronogramaRepository marcoCronogramaRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    // Endpoint  para adicionar um marco ao cronograma de uma obra

    @PostMapping("/obras/{obraId}cronograma/marcos")
    public ResponseEntity<MarcoCronograma> adicionarMarcoCronograma(@PathVariable Long obrId , @RequestBody MarcoCronograma marcoCronograma){
        Optional<Cronograma> cronogramaData = cronogramaRepository.findByObraId(obrId);
        if (cronogramaData.isEmpty() || cronogramaData.get().getObra().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            marcoCronograma.setCronograma(cronogramaData.get());
            MarcoCronograma novoMarco = marcoCronogramaRepository.save(marcoCronograma);
            return new ResponseEntity<>(novoMarco,HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
