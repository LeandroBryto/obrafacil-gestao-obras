package leandro.dev.gestao_obras.model;

import jakarta.persistence.*;
import leandro.dev.gestao_obras.enums.StatusObra;
import leandro.dev.gestao_obras.enums.TipoObra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "obras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_incio")
    private LocalDate dataInicio;

    @Column(name = "data_termino_prevista")
    private LocalDate dataTerminoPrevista;

    @Column(name = "responvavel_tecnico")
    private String responsavelTecnico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_obra", nullable = false)
    private TipoObra tipoObra;

    @Column(name = "area_total")
    private Double areaTotal;

    @Column(name = "valor_orcado")
    private BigDecimal valorOrcado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusObra status;

    @Lob // para texto mais longos
    private String descricao;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean arquivado = false; // novo campo para arquivamento lógico

    @OneToMany(mappedBy = "obra",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Etapa> etapas = new ArrayList<>();

}
