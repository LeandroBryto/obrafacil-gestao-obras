package leandro.dev.gestao_obras.controller;


import jakarta.transaction.Transactional;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    @PostMapping("/obras/{obraId}/etapas/reordenar")
    @Transactional
    public ResponseEntity<List<Etapa>> reordenarEtapas(@PathVariable Long obraId, @RequestBody List<Long> idsOrdenados){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Etapa> etapasDaObra = etapaRepository.findByObraIdOrderByOrdemAsc(obraId);
        if (etapasDaObra.size()!= idsOrdenados.size()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);// numero de IDs nao corresponde
        }
        Map<Long, Etapa> mapaEtapas = etapasDaObra.stream().collect(Collectors.toMap(Etapa::getId, e -> e));

        try {
            for (int i = 0; i< idsOrdenados.size(); i++){
                Long etapaId = idsOrdenados.get(i);
                Etapa etapa = mapaEtapas.get(etapaId);
                if (etapa == null){
                    throw new IllegalArgumentException("ID de etapa inválido na lista:" + etapaId);
                }
                etapa.setOrdem(i + 1); // Define a nova ordem (base 1)
                etapaRepository.save(etapa);
            }
            // Retorna a lista reordenada
            List<Etapa> etapasReordenadas = etapaRepository.findByObraIdOrderByOrdemAsc(obraId);
            return new ResponseEntity<>(etapasReordenadas, HttpStatus.OK);
        }catch (Exception e ){
            // Loggar erro
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint para gerar um relatório simples de progresso da obra
    @GetMapping("/obras/{obraId}relario-progresso")
    public ResponseEntity<Map<String,Object>> gerarRelatorioProgresso(@PathVariable Long obraId){
        Optional<Obra> obraData = obraRepository.findById(obraId);
        if (obraData.isEmpty() || obraData.get().isArquivado()){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Obra obra = obraData.get();
        List<Etapa> etapas = etapaRepository.findByObraIdOrderByOrdemAsc(obraId);

        // calcula progresso geral simples ( média dos percentuais das etapas)
        double progressoGeral = etapas.stream()
                .mapToInt(Etapa::getPercentualConclusao)
                .average()
                .orElse(0.0);

        Map<String, Object> relatorio = Map.of(
                "obraId", obra.getId(),
                "obraNome", obra.getNome(),
                "statusObra", obra.getStatus(),
                "progressoGeralPercentual", String.format("%.2f", progressoGeral),
                "totalEtapas", etapas.size(),
                "etapas", etapas
        );
        return new ResponseEntity<>(relatorio,HttpStatus.OK);

    }
    // Endpoint para deletar uma etapa
    @DeleteMapping("/etapa/{etapaId}")
    @Transactional
    public ResponseEntity<HttpStatus> deletarEtapa(@PathVariable Long etapaId){
        Optional<Etapa> etapaData = etapaRepository.findById(etapaId);
        if (etapaData.isPresent() && !etapaData.get().getObra().isArquivado()){
            try {
                // Deleta itens dp checklist associados (se houver cascade=REMOVE)
                // OU deleta manualmente
                checkListItemRepository.deleteAll(checkListItemRepository.findByEtapaId(etapaId));
                etapaRepository.deleteById(etapaId);
                // reajustar a ordem das etapas restantes pode ser necessario aqui
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }catch (Exception e ){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
