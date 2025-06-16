package leandro.dev.gestao_obras.controller;

import leandro.dev.gestao_obras.model.Cliente;
import leandro.dev.gestao_obras.model.Usuario;
import leandro.dev.gestao_obras.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Cliente> getAllCliente(){
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id){
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente){
        return clienteRepository.save(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id,@RequestBody Cliente clienteDetails){
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNome(clienteDetails.getNome());
                    cliente.setEmail(clienteDetails.getEmail());
                    cliente.setTelefone(clienteDetails.getTelefone());
                    return ResponseEntity.ok(clienteRepository.save(cliente));
                }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id){
        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.delete(cliente);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

}
