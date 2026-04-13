package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.entity.ZonaSegura;
import com.antibankfraud.backend.repository.UsuarioRepository;
import com.antibankfraud.backend.repository.ZonaSeguraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZonaSeguraService {

    @Autowired
    private ZonaSeguraRepository zonaSeguraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ZonaSegura adicionar(Long usuarioId, ZonaSegura zona) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        zona.setUsuario(usuario);
        return zonaSeguraRepository.save(zona);
    }

    public List<ZonaSegura> listarPorUsuario(Long usuarioId) {
        return zonaSeguraRepository.findByUsuarioId(usuarioId);
    }

    public void deletar(Long zonaId) {
        zonaSeguraRepository.deleteById(zonaId);
    }

    public ZonaSegura atualizar(Long zonaId, ZonaSegura dadosNovos) {
        ZonaSegura zona = zonaSeguraRepository.findById(zonaId)
                .orElseThrow(() -> new RuntimeException("Zona segura não encontrada."));
        zona.setDescricao(dadosNovos.getDescricao());
        zona.setLatitude(dadosNovos.getLatitude());
        zona.setLongitude(dadosNovos.getLongitude());
        zona.setRaioMetros(dadosNovos.getRaioMetros());
        return zonaSeguraRepository.save(zona);
    }
}