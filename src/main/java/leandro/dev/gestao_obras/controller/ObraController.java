package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.enums.StatusObra;
import leandro.dev.gestao_obras.model.Obra;
import leandro.dev.gestao_obras.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador de obras do sistema.
 * Lida com endpoints de cadastro, edição, status e arquivamento.
 *
 * @author Leandro
 * @since 2025-06-15
 * @version 1.0 - finalizado em 2025-06-23
 */


@RestController
@RequestMapping("/api/obras")
public class ObraController {

    @Autowired
    private ObraRepository obraRepository;

    @PostMapping
    public ResponseEntity<Obra> cadastraObra(@RequestBody Obra obra) {
        try {
            obra.setArquivado(false);// Garante que novas obras nao sao criadas arquivasa
            Obra novaObra = obraRepository.save(obra);
            return new ResponseEntity<>(novaObra, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // endpoint para listar todas as obras (com filtros opcionais)

    @GetMapping
    public ResponseEntity<List<Obra>> listaObras(
            @RequestParam(required = false) StatusObra status,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "false") boolean incluirArquivadas) {
        try {
            List<Obra> obras = obraRepository.findAll();

            // aplica filtro de arquivadas
            if (!incluirArquivadas) {
                obras = obras.stream().filter(o -> !o.isArquivado()).collect(Collectors.toList());
            }
            //aplica filtros adicionais se fornecidos
            if (status != null) {
                obras = obras.stream().filter(o -> o.getStatus() == status).collect(Collectors.toList());
            }
            if (cliente != null && !cliente.isEmpty()) {
                obras = obras.stream().filter(o -> o.getCliente().getNome().equalsIgnoreCase(cliente)).collect(Collectors.toList());
            }
            // filtro de periodo (considerando data de inicio da obra)
            if (dataInicio != null) {
                obras = obras.stream().filter(o -> o.getDataInicio() != null && !o.getDataInicio().isBefore(dataInicio)).collect((Collectors.toList()));
            }
            if (obras.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(obras, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para buscar uma obra por ID
    @GetMapping("/{id}")
    public ResponseEntity<Obra> buscarObraPorId(@PathVariable("id") Long id) {
        Optional<Obra> obraData = obraRepository.findById(id);
        // Retorna 404 se estiver arquivada e nao for explicitamente pedido  para incluir arquivadas (poderia ser um parâmetro
        if (obraData.isPresent() && !obraData.get().isArquivado()) {
            return new ResponseEntity<>(obraData.get(), HttpStatus.OK);
        } else if (obraData.isPresent() && obraData.get().isArquivado()) {
            // poderia retornar um status diferente ou permitir acesso baseado em permissão
            return new ResponseEntity<>(HttpStatus.GONE); // ou not_found
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para atualizar informaçoes de uma obra
    @PutMapping("/{id}")
    public ResponseEntity<Obra> atualizarObra(@PathVariable("id") Long id ,@RequestBody Obra obraAtualizada){
        Optional<Obra> obraData = obraRepository.findById(id);
        if (obraData.isPresent() && !obraData.get().isArquivado()){
            Obra obraExistente = obraData.get();
            // Atualizar os campos ( execeto ID e arquivado)
            obraExistente.setNome(obraAtualizada.getNome());
            obraExistente.setEndereco(obraAtualizada.getEndereco());
            obraExistente.setCliente(obraAtualizada.getCliente());
            obraExistente.setDataInicio(obraAtualizada.getDataInicio());
            obraExistente.setDataTerminoPrevista(obraAtualizada.getDataTerminoPrevista());
            obraExistente.setResponsavelTecnico(obraAtualizada.getResponsavelTecnico());
            obraExistente.setTipoObra(obraAtualizada.getTipoObra());
            obraExistente.setAreaTotal(obraAtualizada.getAreaTotal());
            obraExistente.setValorOrcado(obraAtualizada.getValorOrcado());
            obraExistente.setStatus(obraAtualizada.getStatus());
            obraExistente.setDescricao(obraAtualizada.getDescricao());

            return new ResponseEntity<>(obraRepository.save(obraExistente),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }
    // endpoint para alterar o status de uma obra
    @PatchMapping("/{id}/status")
    public ResponseEntity<Obra> alterarStatusObra(@PathVariable("id") Long id, @RequestBody StatusObra novoStatus) {
        Optional<Obra> obraData = obraRepository.findById(id);

        if (obraData.isPresent() && !obraData.get().isArquivado()) {
            Obra obra = obraData.get();
            obra.setStatus(novoStatus); // OK, se novoStatus for compatível com o tipo do campo status
            return new ResponseEntity<>(obraRepository.save(obra), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Endpoint para arquivar uma obra (deleçao lógica)
    @DeleteMapping("{/id}")
    public ResponseEntity<HttpStatus> arquivarObra(@PathVariable("id") Long id ){
        Optional<Obra> obraData = obraRepository.findById(id);
        if (obraData.isPresent()){
            Obra obra = obraData.get();
            obra.setArquivado(true);
            obraRepository.save(obra);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Endpoint para Desarquivar uma obra
    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Obra> desarquivarObra(@PathVariable("id") Long id ){
        Optional<Obra> obraDate = obraRepository.findById(id);

        if (obraDate.isPresent()){
            Obra obra = obraDate.get();
            if (!obra.isArquivado()){
                // Pode retonar um erro ou simplesmente a obra como está
                return new ResponseEntity<>(obra,HttpStatus.OK); // ja está desarquivada
            }
            obra.setArquivado(false);
            return new ResponseEntity<>(obraRepository.save(obra),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpont para obter resumo de progresso de obra

    @GetMapping("/{id}/resumo")
    public ResponseEntity<String> getResumoObra(@PathVariable("id") Long id){
        Optional<Obra> obraData = obraRepository.findById(id);
        if (obraData.isPresent()){
            // TODO : IMPLEMENTAR LÓGICA PARA GERAR UM RESUMO DO PROGRESSO DA OBRA.
            // ISSO PODE ENVOLVER A CONSULTA DE ETAPAS , CHECKLIST
            return new ResponseEntity<>("RESUMO DE OBRA"+ id + "(a ser implementado)",HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

