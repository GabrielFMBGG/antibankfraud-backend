package com.antibankfraud.backend.dto;

import lombok.Data;

@Data
public class OTPRequestDTO {
    private String email;
    private String codigo;
}