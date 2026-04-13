package com.antibankfraud.backend.dto;

import lombok.Data;

@Data
public class ZonaSeguraDTO {
    private Double latitude;
    private Double longitude;
    private Double raioMetros;
    private String descricao;
}