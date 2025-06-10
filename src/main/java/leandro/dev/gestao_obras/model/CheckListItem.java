package leandro.dev.gestao_obras.model;

import jakarta.persistence.*;
import leandro.dev.gestao_obras.enums.StatusChecklistItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "checklist_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Etapa_id", nullable = false)
    private Etapa etapa;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChecklistItem status;

    private String responsavel;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

}
