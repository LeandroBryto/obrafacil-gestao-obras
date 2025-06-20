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

@RestController
@RequestMapping("/api/obras")
public class ObraCntroller {

    @Autowired
    private ObraRepository obraRepository;

    @PostMapping
    public ResponseEntity<Obra> cadastraObra(@RequestBody Obra obra){
        try {
            obra.setArquivado(false);// Garante que novas obras nao sao criadas arquivasa
            Obra novaObra = obraRepository.save(obra);
            return new ResponseEntity<>(novaObra, HttpStatus.CREATED);
        } catch (Exception e ){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // endpoint para listar todas as obras (com filtros opcionais)

    @GetMapping
    public ResponseEntity<List<Obra>> listaObras(
            @RequestParam(required = false)StatusObra status,
            @RequestParam(required = false)String cliente,
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataInicio,
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataFim,
            @RequestParam(defaultValue = "false") boolean incluirArquivadas){
        try {
            List<Obra> obras = obraRepository.findAll();

            // aplica filtro de arquivadas
            if (!incluirArquivadas){
                obras = obras.stream().filter( o->!o.isArquivado()).collect(Collectors.toList());
            }
            //aplica filtros adicionais se fornecidos
            if (status != null){
                obras = obras.stream().filter(o -> o.getStatus() == status).collect(Collectors.toList());
            }
            if (cliente != null && !cliente.isEmpty()){
                obras = obras.stream().filter(o -> o.getCliente().getNome().equalsIgnoreCase(cliente)).collect(Collectors.toList());
            }
            // filtro de periodo (considerando data de inicio da obra)
            if (dataInicio != null){
                obras = obras.stream().filter( o -> o.getDataInicio() != null && !o.getDataInicio().isBefore(dataInicio)).collect((Collectors.toList()));
            }
            if (obras.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(obras, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint para buscar uma obra por ID
    GetMapping("/{id}")
        public ResponseEntity<Obra> buscarObraPorId(@PathVariable("id") Long id){
            Optional<Obra> obraData = obraRepository.findById(id);
            // Retorna 404 se estiver arquivada e nao for explicitamente pedido  para incluir arquivadas (poderia ser um parâmetro
            if (obraData.isPresent() && !obraData.get().isArquivado()){
                return new ResponseEntity<>(obraData)
            }
        }
}
