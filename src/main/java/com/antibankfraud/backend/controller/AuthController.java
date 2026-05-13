package com.antibankfraud.backend.controller;

import com.antibankfraud.backend.config.JwtUtil;
import com.antibankfraud.backend.dto.LoginRequestDTO;
import com.antibankfraud.backend.dto.LoginResponseDTO;
import com.antibankfraud.backend.dto.OTPRequestDTO;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.service.AuthService;
import com.antibankfraud.backend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario criado = usuarioService.criar(usuario);
            return ResponseEntity.ok(criado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto,
                                   HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            Usuario usuario = authService.login(
                dto.getEmail(), dto.getSenha(),
                dto.getLatitude(), dto.getLongitude(), ip
            );
            String token = jwtUtil.gerarToken(usuario.getEmail());
            return ResponseEntity.ok(new LoginResponseDTO(
                usuario.getId(), token, usuario.getNome(),
                usuario.getEmail(), usuario.isModoRuaAtivo()
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ACESSO_NEGADO_FORA_DA_ZONA")) {
                return ResponseEntity.status(403).body(
                    "Acesso negado: você está fora da sua zona segura. Use o acesso de emergência."
                );
            }
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/otp/solicitar")
    public ResponseEntity<?> solicitarOTP(@RequestParam String email) {
        try {
            authService.solicitarOTP(email);
            return ResponseEntity.ok("Código enviado para o email cadastrado.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/emergencia")
    public ResponseEntity<?> loginEmergencia(@Valid @RequestBody OTPRequestDTO dto,
                                              HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            Usuario usuario = authService.loginEmergencia(dto.getEmail(), dto.getCodigo(), ip);
            String token = jwtUtil.gerarToken(usuario.getEmail());
            return ResponseEntity.ok(new LoginResponseDTO(
                usuario.getId(), token, usuario.getNome(),
                usuario.getEmail(), usuario.isModoRuaAtivo()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}