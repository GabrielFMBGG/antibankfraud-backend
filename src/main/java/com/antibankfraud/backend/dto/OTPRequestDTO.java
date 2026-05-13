package com.antibankfraud.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OTPRequestDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 6, max = 6, message = "Código deve ter exatamente 6 dígitos")
    private String codigo;
}