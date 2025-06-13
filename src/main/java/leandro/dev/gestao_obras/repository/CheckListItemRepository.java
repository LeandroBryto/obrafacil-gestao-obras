package leandro.dev.gestao_obras.repository;

import leandro.dev.gestao_obras.model.CheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckListItemRepository extends JpaRepository<CheckListItem , Long> {

    List<CheckListItem> findByEtapaId(Long etapaId);


}
