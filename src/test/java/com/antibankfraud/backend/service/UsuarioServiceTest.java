package com.antibankfraud.backend.service;

import com.antibankfraud.backend.dto.AtualizarUsuarioDTO;
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
@DisplayName("UsuarioService — cadastro, atualização e Modo Rua")
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder   passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senha123");
        usuario.setModoRuaAtivo(false);
    }

    // ===================================================================
    // criar()
    // ===================================================================

    @Test
    @DisplayName("criar: e-mail novo deve criar usuário com senha hasheada")
    void criar_emailNovo_criausuarioComSenhaHash() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashGerado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario criado = usuarioService.criar(usuario);

        assertNotNull(criado);
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(usuario);
        assertEquals("$2a$10$hashGerado", usuario.getSenha());
    }

    @Test
    @DisplayName("criar: e-mail duplicado deve lançar RuntimeException")
    void criar_emailDuplicado_lancaException() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.criar(usuario));

        assertTrue(ex.getMessage().contains("E-mail já cadastrado"));
        verify(usuarioRepository, never()).save(any());
    }

    // ===================================================================
    // atualizar()
    // ===================================================================

    @Test
    @DisplayName("atualizar: dados válidos devem persistir as alterações")
    void atualizar_dadosValidos_persisteAlteracoes() {
        AtualizarUsuarioDTO dto = new AtualizarUsuarioDTO();
        dto.setNome("João Atualizado");
        dto.setTelefone("11999998888");
        dto.setEmailSecundario("joao.sec@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario atualizado = usuarioService.atualizar(1L, dto);

        assertNotNull(atualizado);
        assertEquals("João Atualizado", usuario.getNome());
        assertEquals("11999998888", usuario.getTelefone());
        assertEquals("joao.sec@email.com", usuario.getEmailSecundario());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("atualizar: usuário inexistente deve lançar RuntimeException")
    void atualizar_usuarioInexistente_lancaException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        AtualizarUsuarioDTO dto = new AtualizarUsuarioDTO();
        dto.setNome("Qualquer");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.atualizar(99L, dto));

        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
    }

    // ===================================================================
    // alternarModoRua()
    // ===================================================================

    @Test
    @DisplayName("alternarModoRua: modo inativo → deve ativar e salvar")
    void alternarModoRua_inativo_ativa() {
        usuario.setModoRuaAtivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.alternarModoRua(1L);

        assertTrue(resultado.isModoRuaAtivo(), "Modo Rua deve estar ativo após alternância");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("alternarModoRua: modo ativo → deve desativar e salvar")
    void alternarModoRua_ativo_desativa() {
        usuario.setModoRuaAtivo(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.alternarModoRua(1L);

        assertFalse(resultado.isModoRuaAtivo(), "Modo Rua deve estar inativo após alternância");
    }
}
