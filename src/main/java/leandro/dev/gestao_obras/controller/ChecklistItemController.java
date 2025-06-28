package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.model.CheckListItem;
import leandro.dev.gestao_obras.model.Etapa;
import leandro.dev.gestao_obras.repository.CheckListItemRepository;
import leandro.dev.gestao_obras.repository.EtapaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.IllegalFormatCodePointException;
import java.util.Optional;

@RestController
@RequestMapping("/api/itens-Checklist")
public class ChecklistItemController {
    @Autowired
    private CheckListItemRepository checkListItemRepository;

    @Autowired
    private EtapaRepository etapaRepository;

    // Endpoint para criar um novo item de ckecklist para uma etapa específica
    public ResponseEntity<CheckListItem> criaChecklistItem(@PathVariable Long etapaId, @RequestBody CheckListItem checkListItem){
        Optional<Etapa> etapaData = etapaRepository.findById(etapaId);
        // Verifica se a etapa existe e se a obra associada não está arquivada
        if (etapaData.isEmpty() || etapaData.get().getObra().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // etapa nao encontrada ou pertence a obra arquivada
        }
        try {
            checkListItem.setEtapa(etapaData.get());
            CheckListItem novoItem = checkListItemRepository.save(checkListItem);
            // TODO: adcionar lógica para atualizar p percentual de conclusão da etapa pai
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
