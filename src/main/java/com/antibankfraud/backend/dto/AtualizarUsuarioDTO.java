package com.antibankfraud.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AtualizarUsuarioDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    // E-mail secundário opcional — validado só se preenchido
    @Email(message = "E-mail secundário inválido")
    private String emailSecundario;

    // Telefone opcional — validado só se preenchido
    @Pattern(
        regexp = "^(\\d{10,11})?$",
        message = "Telefone deve ter 10 ou 11 dígitos"
    )
    private String telefone;
}