package leandro.dev.gestao_obras.model;

import jakarta.persistence.*;
import leandro.dev.gestao_obras.enums.StatusCronograma;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cronogramas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_id", nullable = false, unique = true)
    private Obra obra;

    @Column(name = "data_inicio_projeto",nullable = false)
    private LocalDate dataInicioProjeto;

    @Column(name = "data_termino_prevista", nullable = false)
    private LocalDate dataTerminoPrevista;

    @Column(name = "data_termino_atual") // Pode ser nulo inicialmente
    private LocalDate dataTerminoAtuL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_geral")
    private StatusCronograma statusGeral;

    @Column(name = "dias_atraso_adiantamento")
    private Integer diasAtrasoAdiantamento;    // Positivo para adiantamento , negativo para atraso

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cronograma_marcos", joinColumns = @JoinColumn(name = "cronograma_id"))
    @Column(name = "marco")
    private List<String> marcosImportantes = new ArrayList<>();

    private String status;

}
