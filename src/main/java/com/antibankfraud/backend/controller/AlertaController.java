package com.antibankfraud.backend.controller;

import com.antibankfraud.backend.entity.Alerta;
import com.antibankfraud.backend.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Alerta>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(alertaService.listarPorUsuario(usuarioId));
    }
}