package leandro.dev.gestao_obras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "marcos_cronograma")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarcoCronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cronograma_id",nullable = false)
    private Cronograma cronograma;

    @Column(nullable = false)
    private String name;

    @Column(name = "data_prevista",nullable = false)
    private LocalDate dataPrevista;

    private String descricao;

    @Column(name = "data_conlusao")
    private LocalDate dataConclusao;

    private boolean concluido = false;

}
