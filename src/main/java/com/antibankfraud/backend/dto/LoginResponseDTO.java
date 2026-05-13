package com.antibankfraud.backend.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Long id;
    private String token;
    private String nome;
    private String email;
    private boolean modoRuaAtivo;

    public LoginResponseDTO(Long id, String token, String nome, String email, boolean modoRuaAtivo) {
        this.id = id;
        this.token = token;
        this.nome = nome;
        this.email = email;
        this.modoRuaAtivo = modoRuaAtivo;
    }
}