package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.entity.ZonaSegura;
import com.antibankfraud.backend.repository.UsuarioRepository;
import com.antibankfraud.backend.repository.ZonaSeguraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ZonaSeguraService — CRUD de zonas seguras")
class ZonaSeguraServiceTest {

    @Mock private ZonaSeguraRepository zonaSeguraRepository;
    @Mock private UsuarioRepository    usuarioRepository;

    @InjectMocks
    private ZonaSeguraService zonaSeguraService;

    private Usuario    usuario;
    private ZonaSegura zona;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");

        zona = new ZonaSegura();
        zona.setId(10L);
        zona.setDescricao("Minha casa");
        zona.setLatitude(-23.5505);
        zona.setLongitude(-46.6333);
        zona.setRaioMetros(500.0);
    }

    // ===================================================================
    // adicionar()
    // ===================================================================

    @Test
    @DisplayName("adicionar: usuário existente deve salvar zona com vínculo correto")
    void adicionar_usuarioExistente_salvaZona() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(zonaSeguraRepository.save(any(ZonaSegura.class))).thenReturn(zona);

        ZonaSegura resultado = zonaSeguraService.adicionar(1L, zona);

        assertNotNull(resultado);
        assertEquals(usuario, zona.getUsuario());
        verify(zonaSeguraRepository).save(zona);
    }

    @Test
    @DisplayName("adicionar: usuário inexistente deve lançar RuntimeException")
    void adicionar_usuarioInexistente_lancaException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> zonaSeguraService.adicionar(99L, zona));

        assertTrue(ex.getMessage().contains("Usuário não encontrado"));
        verify(zonaSeguraRepository, never()).save(any());
    }

    // ===================================================================
    // atualizar()
    // ===================================================================

    @Test
    @DisplayName("atualizar: zona existente deve persistir novos dados")
    void atualizar_zonaExistente_persisteDados() {
        ZonaSegura novosDados = new ZonaSegura();
        novosDados.setDescricao("Trabalho");
        novosDados.setLatitude(-23.5617);
        novosDados.setLongitude(-46.6560);
        novosDados.setRaioMetros(300.0);

        when(zonaSeguraRepository.findById(10L)).thenReturn(Optional.of(zona));
        when(zonaSeguraRepository.save(any(ZonaSegura.class))).thenAnswer(inv -> inv.getArgument(0));

        ZonaSegura atualizada = zonaSeguraService.atualizar(10L, novosDados);

        assertEquals("Trabalho",  atualizada.getDescricao());
        assertEquals(-23.5617,    atualizada.getLatitude());
        assertEquals(-46.6560,    atualizada.getLongitude());
        assertEquals(300.0,       atualizada.getRaioMetros());
        verify(zonaSeguraRepository).save(zona);
    }

    @Test
    @DisplayName("atualizar: zona inexistente deve lançar RuntimeException")
    void atualizar_zonaInexistente_lancaException() {
        when(zonaSeguraRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> zonaSeguraService.atualizar(999L, zona));

        assertTrue(ex.getMessage().contains("Zona segura não encontrada"));
    }

    // ===================================================================
    // listarPorUsuario() e deletar()
    // ===================================================================

    @Test
    @DisplayName("listarPorUsuario: deve retornar lista do repositório")
    void listarPorUsuario_retornaListaCorreta() {
        when(zonaSeguraRepository.findByUsuarioId(1L)).thenReturn(List.of(zona));

        List<ZonaSegura> resultado = zonaSeguraService.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("Minha casa", resultado.get(0).getDescricao());
    }

    @Test
    @DisplayName("deletar: deve chamar deleteById no repositório")
    void deletar_deveChamarDeleteById() {
        doNothing().when(zonaSeguraRepository).deleteById(10L);

        zonaSeguraService.deletar(10L);

        verify(zonaSeguraRepository).deleteById(10L);
    }
}
