package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.ZonaSegura;
import com.antibankfraud.backend.repository.ZonaSeguraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LocalizacaoService {

    @Autowired
    private ZonaSeguraRepository zonaSeguraRepository;

    // Fórmula de Haversine — calcula distância entre dois pontos geográficos em metros
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RAIO_TERRA = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA * c;
    }

    // Verifica se a localização está em ALGUMA zona segura do usuário.
    // Se não há zonas cadastradas, libera o acesso — o usuário ainda
    // não configurou restrições, então não deve ser bloqueado.
    public boolean estaNaZonaSegura(Long usuarioId, double lat, double lon) {
        List<ZonaSegura> zonas = zonaSeguraRepository.findByUsuarioId(usuarioId);

        if (zonas.isEmpty()) return true;

        for (ZonaSegura zona : zonas) {
            double distancia = calcularDistancia(lat, lon, zona.getLatitude(), zona.getLongitude());
            if (distancia <= zona.getRaioMetros()) {
                return true;
            }
        }
        return false;
    }
}