package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.Alerta;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — lógica de autenticação e Modo Rua")
class AuthServiceTest {

    @Mock private UsuarioRepository  usuarioRepository;
    @Mock private PasswordEncoder    passwordEncoder;
    @Mock private LocalizacaoService localizacaoService;
    @Mock private AlertaService      alertaService;
    @Mock private OTPService         otpService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    private static final String EMAIL = "joao@email.com";
    private static final String SENHA = "senha123";
    private static final String IP    = "127.0.0.1";
    private static final double LAT   = -23.5505;
    private static final double LON   = -46.6333;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail(EMAIL);
        usuario.setSenha("$2a$10$hash");
        usuario.setModoRuaAtivo(false);
    }

    // ===================================================================
    // login()
    // ===================================================================

    @Test
    @DisplayName("login: credenciais válidas sem Modo Rua deve retornar usuário")
    void login_credenciaisValidasSemModoRua_retornaUsuario() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(true);

        Usuario resultado = authService.login(EMAIL, SENHA, LAT, LON, IP);

        assertNotNull(resultado);
        assertEquals(EMAIL, resultado.getEmail());
        verify(alertaService).registrar(
                eq(usuario), anyString(),
                eq(Alerta.TipoAlerta.LOGIN_SUCESSO),
                eq(LAT), eq(LON), isNull(), eq(IP)
        );
    }

    @Test
    @DisplayName("login: e-mail inexistente deve lançar RuntimeException")
    void login_emailInexistente_lancaException() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(EMAIL, SENHA, LAT, LON, IP));

        assertTrue(ex.getMessage().contains("Email ou senha inválidos"));
        verify(alertaService, never()).registrar(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("login: senha incorreta deve lançar RuntimeException")
    void login_senhaIncorreta_lancaException() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(EMAIL, SENHA, LAT, LON, IP));

        assertTrue(ex.getMessage().contains("Email ou senha inválidos"));
    }

    @Test
    @DisplayName("login: Modo Rua ativo + dentro da zona deve liberar acesso")
    void login_modoRuaAtivoEDentroZona_retornaUsuario() {
        usuario.setModoRuaAtivo(true);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(true);
        when(localizacaoService.estaNaZonaSegura(usuario.getId(), LAT, LON)).thenReturn(true);

        Usuario resultado = authService.login(EMAIL, SENHA, LAT, LON, IP);

        assertNotNull(resultado);
        verify(localizacaoService).estaNaZonaSegura(usuario.getId(), LAT, LON);
    }

    @Test
    @DisplayName("login: Modo Rua ativo + fora da zona deve lançar ACESSO_NEGADO")
    void login_modoRuaAtivoEForaZona_lancaAcessoNegado() {
        usuario.setModoRuaAtivo(true);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(true);
        when(localizacaoService.estaNaZonaSegura(usuario.getId(), LAT, LON)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(EMAIL, SENHA, LAT, LON, IP));

        assertEquals("ACESSO_NEGADO_FORA_DA_ZONA", ex.getMessage());
        verify(alertaService).registrar(
                eq(usuario), anyString(),
                eq(Alerta.TipoAlerta.ACESSO_NEGADO),
                eq(LAT), eq(LON), isNull(), eq(IP)
        );
    }

    @Test
    @DisplayName("login: Modo Rua ativo + sem localização deve bloquear imediatamente")
    void login_modoRuaAtivoSemLocalizacao_lancaAcessoNegado() {
       usuario.setModoRuaAtivo(true);
       when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
       when(passwordEncoder.matches(SENHA, usuario.getSenha())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.login(EMAIL, SENHA, null, null, IP));

        assertEquals("ACESSO_NEGADO_FORA_DA_ZONA", ex.getMessage());
        verify(localizacaoService, never()).estaNaZonaSegura(anyLong(), anyDouble(), anyDouble());
}

    // ===================================================================
    // loginEmergencia()
    // ===================================================================

    @Test
    @DisplayName("loginEmergencia: código OTP válido deve retornar usuário")
    void loginEmergencia_codigoValido_retornaUsuario() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(otpService.validar(usuario.getId(), "123456")).thenReturn(true);

        Usuario resultado = authService.loginEmergencia(EMAIL, "123456", IP);

        assertNotNull(resultado);
        verify(alertaService).registrar(
                eq(usuario), anyString(),
                eq(Alerta.TipoAlerta.ACESSO_EMERGENCIA),
                isNull(), isNull(), isNull(), eq(IP)
        );
    }

    @Test
    @DisplayName("loginEmergencia: código OTP inválido ou expirado deve lançar exception")
    void loginEmergencia_codigoInvalido_lancaException() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(otpService.validar(usuario.getId(), "000000")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.loginEmergencia(EMAIL, "000000", IP));

        assertTrue(ex.getMessage().contains("Código inválido ou expirado"));
    }
}
