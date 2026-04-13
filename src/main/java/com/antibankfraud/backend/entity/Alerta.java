package com.antibankfraud.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String mensagem;

    // Tipo: ACESSO_NEGADO, ACESSO_EMERGENCIA, LOGIN_SUCESSO
    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;

    // Localização de onde veio a tentativa
    @Column(name = "tentativa_lat")
    private Double tentativaLat;

    @Column(name = "tentativa_lon")
    private Double tentativaLon;

    // Cidade/região aproximada (ex: "São Paulo, SP")
    @Column(name = "tentativa_regiao")
    private String tentativaRegiao;

    // IP de onde veio a requisição
    @Column(name = "ip_origem")
    private String ipOrigem;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @PrePersist
    public void prePersist() {
        this.dataHora = LocalDateTime.now();
    }

    public enum TipoAlerta {
        ACESSO_NEGADO,
        ACESSO_EMERGENCIA,
        LOGIN_SUCESSO
    }
}