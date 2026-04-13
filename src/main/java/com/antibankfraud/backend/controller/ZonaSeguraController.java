package com.antibankfraud.backend.controller;

import com.antibankfraud.backend.dto.ZonaSeguraDTO;
import com.antibankfraud.backend.entity.ZonaSegura;
import com.antibankfraud.backend.service.ZonaSeguraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zonas-seguras")
public class ZonaSeguraController {

    @Autowired
    private ZonaSeguraService zonaSeguraService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> adicionar(@PathVariable Long usuarioId,
                                        @RequestBody ZonaSeguraDTO dto) {
        try {
            ZonaSegura zona = new ZonaSegura();
            zona.setLatitude(dto.getLatitude());
            zona.setLongitude(dto.getLongitude());
            zona.setRaioMetros(dto.getRaioMetros());
            zona.setDescricao(dto.getDescricao());
            return ResponseEntity.ok(zonaSeguraService.adicionar(usuarioId, zona));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ZonaSegura>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(zonaSeguraService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/{zonaId}")
    public ResponseEntity<?> atualizar(@PathVariable Long zonaId,
                                        @RequestBody ZonaSeguraDTO dto) {
        try {
            ZonaSegura zona = new ZonaSegura();
            zona.setLatitude(dto.getLatitude());
            zona.setLongitude(dto.getLongitude());
            zona.setRaioMetros(dto.getRaioMetros());
            zona.setDescricao(dto.getDescricao());
            return ResponseEntity.ok(zonaSeguraService.atualizar(zonaId, zona));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{zonaId}")
    public ResponseEntity<?> deletar(@PathVariable Long zonaId) {
        zonaSeguraService.deletar(zonaId);
        return ResponseEntity.ok("Zona segura removida com sucesso.");
    }
}