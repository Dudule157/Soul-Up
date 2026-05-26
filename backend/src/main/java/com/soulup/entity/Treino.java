package com.soulup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "treinos")
@Getter
@Setter
@NoArgsConstructor
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 80)
    private String exercicio;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(nullable = false)
    private Integer duracao;

    @Lob
    @Column(nullable = false)
    private String feedback;

    @Column(name = "data_treino", nullable = false)
    private LocalDateTime dataTreino;

    public Treino(Usuario usuario, String exercicio, Integer repeticoes, Integer duracao, String feedback) {
        this.usuario = usuario;
        this.exercicio = exercicio;
        this.repeticoes = repeticoes;
        this.duracao = duracao;
        this.feedback = feedback;
        this.dataTreino = LocalDateTime.now();
    }
}
