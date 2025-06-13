package leandro.dev.gestao_obras.model;

import jakarta.persistence.*;
import leandro.dev.gestao_obras.enums.TipoRegistroDiario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diario_obra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiarioObra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_hora",nullable = false)
    private Obra obra;

    @Column(name = "data_hora", nullable = false)
    private LocalDate dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRegistroDiario tipo;

    @Lob
    @Column(nullable = false)
    private String descricao;

    private String clima;

    @Lob
    @Column(name = "equipw_presente")
    private String equipePresente;

    @Column(name = "registra_por",nullable = false)
    private String registradoPor;


    // Lista de caminhos/URLs das fotos anexadas
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "diario_obra_documentos",joinColumns = @JoinColumn(name = "diario_obra_id"))
    @Column(name = "foto_path")
    private List<String> fotos = new ArrayList<>();


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "diario_obra_documentos",joinColumns = @JoinColumn(name = "diario_obra_id"))
    @Column(name = "documento_id")
    private List<String> documnetas = new ArrayList<>();

    // Vincula opcional com uma etapa especifica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_id")
    private Etapa etapaRelacionada;

    @Column(nullable = false,columnDefinition = "BOOLEAN DEFAUT  FALSE")
    private boolean critico = false;
}
