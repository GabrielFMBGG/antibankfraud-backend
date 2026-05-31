package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.Alerta;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LocalizacaoService localizacaoService;

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private OTPService otpService;

    public Usuario login(String email, String senha, Double lat, Double lon, String ip) {
        // 1. Verifica se o usuário existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos."));

        // 2. Verifica a senha
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos.");
        }

        // 3. Se Modo Rua estiver ativo, localização é OBRIGATÓRIA
        if (usuario.isModoRuaAtivo()) {
            // Bloqueia imediatamente se nenhuma localização foi enviada
            if (lat == null || lon == null) {
                alertaService.registrar(
                    usuario,
                    "Tentativa de acesso bloqueada: localização não fornecida com Modo Rua ativo.",
                    Alerta.TipoAlerta.ACESSO_NEGADO,
                    null, null, null, ip
                );
                throw new RuntimeException("ACESSO_NEGADO_FORA_DA_ZONA");
            }

            boolean naZona = localizacaoService.estaNaZonaSegura(usuario.getId(), lat, lon);

            if (!naZona) {
                // Registra o alerta de acesso negado
                alertaService.registrar(
                    usuario,
                    "Tentativa de acesso bloqueada fora da zona segura.",
                    Alerta.TipoAlerta.ACESSO_NEGADO,
                    lat, lon, null, ip
                );
                throw new RuntimeException("ACESSO_NEGADO_FORA_DA_ZONA");
            }
        }

        // 4. Registra login bem-sucedido
        alertaService.registrar(
            usuario,
            "Login realizado com sucesso.",
            Alerta.TipoAlerta.LOGIN_SUCESSO,
            lat, lon, null, ip
        );

        return usuario;
    }

    public Usuario loginEmergencia(String email, String codigo, String ip) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        boolean valido = otpService.validar(usuario.getId(), codigo);
        if (!valido) {
            throw new RuntimeException("Código inválido ou expirado.");
        }

        alertaService.registrar(
            usuario,
            "Acesso de emergência realizado com sucesso.",
            Alerta.TipoAlerta.ACESSO_EMERGENCIA,
            null, null, null, ip
        );

        return usuario;
    }

    public void solicitarOTP(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        otpService.gerarEEnviar(usuario);
    }
}