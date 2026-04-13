package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.Alerta;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    public Alerta registrar(Usuario usuario, String mensagem,
                            Alerta.TipoAlerta tipo, Double lat,
                            Double lon, String regiao, String ip) {
        Alerta alerta = new Alerta();
        alerta.setUsuario(usuario);
        alerta.setMensagem(mensagem);
        alerta.setTipo(tipo);
        alerta.setTentativaLat(lat);
        alerta.setTentativaLon(lon);
        alerta.setTentativaRegiao(regiao);
        alerta.setIpOrigem(ip);
        return alertaRepository.save(alerta);
    }

    public List<Alerta> listarPorUsuario(Long usuarioId) {
        return alertaRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }
}