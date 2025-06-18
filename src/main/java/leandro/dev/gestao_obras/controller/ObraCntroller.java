package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.model.Obra;
import leandro.dev.gestao_obras.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
