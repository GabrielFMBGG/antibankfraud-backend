package com.antibankfraud.backend.controller;

import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                        @RequestBody Usuario dados) {
        try {
            return ResponseEntity.ok(usuarioService.atualizar(id, dados));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.ok("Usuário removido com sucesso.");
    }

    @PatchMapping("/{id}/modo-rua")
    public ResponseEntity<?> alternarModoRua(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.alternarModoRua(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}