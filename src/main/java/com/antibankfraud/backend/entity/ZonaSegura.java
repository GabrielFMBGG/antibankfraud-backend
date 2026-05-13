package com.antibankfraud.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "zonas_seguras")
public class ZonaSegura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "Latitude inválida")
    @DecimalMax(value = "90.0", message = "Latitude inválida")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude inválida")
    @DecimalMax(value = "180.0", message = "Longitude inválida")
    @Column(nullable = false)
    private Double longitude;

    @NotNull(message = "Raio é obrigatório")
    @Min(value = 100, message = "Raio mínimo é 100 metros")
    @Max(value = 5000, message = "Raio máximo é 5000 metros")
    @Column(name = "raio_metros", nullable = false)
    private Double raioMetros = 500.0;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
    private String descricao;
}