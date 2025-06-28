package leandro.dev.gestao_obras.controller;


import leandro.dev.gestao_obras.enums.StatusEtapa;
import leandro.dev.gestao_obras.model.Etapa;
import leandro.dev.gestao_obras.model.Obra;
import leandro.dev.gestao_obras.repository.CheckListItemRepository;
import leandro.dev.gestao_obras.repository.EtapaRepository;
import leandro.dev.gestao_obras.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EtapaController {

    @Autowired
    private EtapaRepository etapaRepository;

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private CheckListItemRepository checkListItemRepository;

    // Endpoint para criar uma nova etapa para uma obra específica

    @PostMapping("/obra/{obraId}/etapas")
    public ResponseEntity<Etapa> criarEtapa(@PathVariable Long obraId, @RequestBody Etapa etapa){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);// OBRA NÃO ENCONTRADA OU ARQUIVADA
        }
        try {
            etapa.setObra(obraData.get());
            // define a ordem inicial (ultimo + 1)
            List<Etapa> etapasExistentes = etapaRepository.findByObraIdOrderByOrdemAsc(obraId);
            etapa.setOrdem(etapasExistentes.size() + 1);
            Etapa novaEtapa = etapaRepository.save(etapa);
            return new ResponseEntity<>(novaEtapa, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // endpoint para listar todos as etapas de uma obra especifica
    @GetMapping("/obra/{obraId}/etapas")
    public ResponseEntity<List<Etapa>> listarEtapasPorObra(@PathVariable Long  obraId){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // obra não encontrada ou arquivada
        }
        try {
            List<Etapa> etapas = etapaRepository.findByObraIdOrderByOrdemAsc(obraId);
            if (etapas.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(etapas,HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint para buscar uma etapa específica por ID
    @GetMapping("/etapas/{etapaId}")
    public ResponseEntity<Etapa> buscarEtapaPorId(@PathVariable Long etapaId){
        Optional<Etapa> etapaData = etapaRepository.findById(etapaId);
        if (etapaData.isPresent() && !etapaData.get().getObra().isArquivado()){
            return new ResponseEntity<>(etapaData.get(),HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Endpoint para atualizar informçoes de uma etapa
    @PutMapping("/etapas/{etapaId}")
    public ResponseEntity<Etapa> atualizarEtapa(@PathVariable Long etapaId, @RequestBody Etapa etapaAtualizada){
        Optional<Etapa> etapaData = etapaRepository.findById(etapaId);
        if (etapaData.isPresent() && !etapaData.get().getObra().isArquivado()){
            Etapa etapaExistente = etapaData.get();
            // atualiza os campos permitidos
            etapaExistente.setNome(etapaAtualizada.getNome());
            etapaExistente.setDescricao(etapaAtualizada.getDescricao());
            etapaExistente.setDataPrevistaInicio(etapaAtualizada.getDataPrevistaInicio());
            etapaExistente.setDataPrevistaTermino(etapaAtualizada.getDataPrevistaTermino());
            etapaExistente.setDataRealInicio(etapaAtualizada.getDataRealInicio());
            etapaExistente.setDataPrevistaTermino(etapaAtualizada.getDataPrevistaTermino());
            etapaExistente.setResponsavel(etapaAtualizada.getResponsavel());
            etapaExistente.setObservacoes(etapaAtualizada.getObservacoes());
            //etapaExistente.setPercentualConclusao(etapaAtualizada.getPercentualConclusao()); // Percentual e calculado
            //etapaExistente.setStatus(etapaAtualizada.getStatus());
            // Recalcula etatus baseado nas data , se necessario
            // if(etapaExistente.getDataRealInicio() != null && etapaExistente.getDataRealTermino() == null){
            // etapaExistente.setStatus(StatusEtapa.EM_ANDAMENTO);
            return new ResponseEntity<>(etapaRepository.save(etapaExistente),HttpStatus.OK);
       } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Endpoint para atualizar progresso e status de uma etapa(PATCH)
    @PatchMapping("/etapas/{etapaId}/progresso")
    public ResponseEntity<Etapa> ataulizarProgressoEtapa(@PathVariable Long etapaId, @RequestBody Map<String , Object> updates){
        Optional<Etapa> etapaData = etapaRepository.findById(etapaId);
        if (etapaData.isEmpty() || etapaData.get().getObra().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Etapa etapa = etapaData.get();
        boolean updated = false;

        if (updates.containsKey("percentualConclusao")){
            try {
                Integer percentual = Integer.parseInt(updates.get("percentualCoclusao").toString());
                if (percentual >= 0 && percentual <= 100){
                    etapa.setPercentualConclusao(percentual);
                    updated = true;
                }else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);// VALOR INVÁLIDO
                }
            }catch (NumberFormatException e){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // formato invalido
            }
        }
        if (updates.containsKey("status")){
            try {
                StatusEtapa novoStatus = StatusEtapa.valueOf(updates.get("status").toString().toUpperCase());
                etapa.setStatus(novoStatus);
                updated = true;
            }catch (IllegalArgumentException e){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // status invalido
            }
        }
        if (updated){
            // atualiza datas reais se o status mudar para EM_ANDAMENTOS OU CONCLUIDA
            if (etapa.getStatus() == StatusEtapa.EM_ANDAMENTO && etapa.getDataRealInicio() == null){
                etapa.setDataRealInicio(java.time.LocalDate.now());
            }
            if (etapa.getStatus() == StatusEtapa.CONCLUIDO && etapa.getDataRealInicio() == null){
                etapa.setDataRealTermino(java.time.LocalDate.now());
                etapa.setPercentualConclusao(100); // garante 100% ao concluir
            }
            return new ResponseEntity<>(etapaRepository.save(etapa),HttpStatus.OK);
        }else {
            return new ResponseEntity<>(etapa, HttpStatus.OK);// nenhuma alteração válida foi feita
        }
    }

}
