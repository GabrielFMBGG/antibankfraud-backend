package com.antibankfraud.backend.service;

import com.antibankfraud.backend.dto.AtualizarUsuarioDTO;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario criar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Usa DTO específico — sem exigir senha na atualização
    public Usuario atualizar(Long id, AtualizarUsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        usuario.setNome(dto.getNome());

        // Email secundário e telefone são opcionais
        if (dto.getEmailSecundario() != null) {
            usuario.setEmailSecundario(dto.getEmailSecundario().isBlank() ? null : dto.getEmailSecundario());
        }
        if (dto.getTelefone() != null) {
            usuario.setTelefone(dto.getTelefone().isBlank() ? null : dto.getTelefone());
        }

        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario alternarModoRua(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        usuario.setModoRuaAtivo(!usuario.isModoRuaAtivo());
        return usuarioRepository.save(usuario);
    }
}