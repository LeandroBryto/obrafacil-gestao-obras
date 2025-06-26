package leandro.dev.gestao_obras.controller;


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
}
