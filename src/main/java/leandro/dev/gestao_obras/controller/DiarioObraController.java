package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.enums.TipoRegistroDiario;
import leandro.dev.gestao_obras.model.DiarioObra;
import leandro.dev.gestao_obras.model.Etapa;
import leandro.dev.gestao_obras.model.Obra;
import leandro.dev.gestao_obras.repository.DiarioObraRepository;
import leandro.dev.gestao_obras.repository.EtapaRepository;
import leandro.dev.gestao_obras.repository.ObraRepository;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DiarioObraController {

    @Autowired
    private DiarioObraRepository diarioObraRepository;

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private EtapaRepository etapaRepository;

    // Endpoint para criar um novo registro no diario de obra
    @PostMapping("obras/{obraId}/diario")
    private ResponseEntity<DiarioObra> criarRegistroDiario(@PathVariable Long obraId, @RequestBody DiarioObra diarioObra){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            diarioObra.setObra(obraData.get());
            if (diarioObra.getDataHora() == null){
                diarioObra.setDataHora(LocalDateTime.now());
            }
            if (diarioObra.getEtapaRelacionada() != null && diarioObra.getEtapaRelacionada().getId() !=null){
                Optional<Etapa> etapaData = etapaRepository.findById(diarioObra.getEtapaRelacionada().getId());
                if (obraData.isPresent() && etapaData.get().getObra().getId().equals(obraId)){
                    diarioObra.setEtapaRelacionada(etapaData.get());
                }else {
                    diarioObra.setEtapaRelacionada(null);
                }
            }else {
                diarioObra.setEtapaRelacionada(null);
            }
            // salva o registro
            DiarioObra novoRegistro = diarioObraRepository.save(diarioObra);

            // verificar se é critco e dispara notoficação (placeholder)
            if (novoRegistro.isCritico()){
                System.out.println("ALERTA: Ocorrência crítica registrada! ID:" + "obra:" + obraData.get().getNome());
                // TODO:Implementar envio real de notificação (email, SMS, ETC. )
            }
            return new ResponseEntity<>(novoRegistro, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("Erro ao criar registra no diario:" + e.getMessage());
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    // Endpoint para buscar uma registro especifico do diario porID
    @GetMapping("/obras/{obraId}/diario/relatorio")
    public ResponseEntity<DiarioObra> buscarRegistroDiarioPorId(@PathVariable Long diarioId){
        Optional<DiarioObra> diarioData = diarioObraRepository.findById(diarioId);
        if (diarioData.isPresent() && !diarioData.get().getObra().isArquivado()){
            return new ResponseEntity<>(diarioData.get(),HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
