package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente , Long> {
}
