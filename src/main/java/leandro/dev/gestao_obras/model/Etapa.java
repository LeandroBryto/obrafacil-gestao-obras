package leandro.dev.gestao_obras.model;

import jakarta.persistence.*;
import leandro.dev.gestao_obras.enums.StatusEtapa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etapas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_id",nullable = false)
    private Obra obra;

    @Column(nullable = false)
    private String nome;

    @Lob
    private String descricao;

    @Column(nullable = false)
    private Integer ordem;

    @Column(name = "data_prevista_inicio")
    private LocalDate dataPrevistaInicio;

    @Column(name = "data_prevista_termino")
    private LocalDate dataPrevistaTermino;

    @Column(name = "data_real_inicio")
    private LocalDate dataRealInicio;

    @Column(name = "data_real_termino")
    private LocalDate dataRealTermino;

    @Column(name = "percentual_conclucao", columnDefinition = "INT DEFAULT 0")
    private Integer percentualConclusao = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEtapa status;

    private String responsavel;

    @Lob
    private String observacoes;

    // vamos cria um relacionamento one-to-many com checkliestItem

    @OneToMany(mappedBy = "etapa", cascade =CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CheckListItem> checkListItems = new ArrayList<>();

}
