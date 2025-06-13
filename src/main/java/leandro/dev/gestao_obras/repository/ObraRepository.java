package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObraRepository extends JpaRepository<Obra , Long> {

}
