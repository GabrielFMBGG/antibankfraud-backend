package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.ZonaSegura;
import com.antibankfraud.backend.repository.ZonaSeguraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalizacaoService — cálculo Haversine e verificação de zonas")
class LocalizacaoServiceTest {

    @Mock
    private ZonaSeguraRepository zonaSeguraRepository;

    @InjectMocks
    private LocalizacaoService localizacaoService;

    private static final Long USUARIO_ID = 1L;

    // Coordenadas de referência: Av. Paulista, São Paulo
    private static final double LAT_PAULISTA = -23.5631;
    private static final double LON_PAULISTA = -46.6544;

    @BeforeEach
    void setUp() {
        // Instância limpa antes de cada teste
    }

    // ===================================================================
    // calcularDistancia
    // ===================================================================

    @Test
    @DisplayName("calcularDistancia: mesmo ponto deve retornar 0 metros")
    void calcularDistancia_mesmoPonto_retornaZero() {
        double distancia = localizacaoService.calcularDistancia(
                LAT_PAULISTA, LON_PAULISTA,
                LAT_PAULISTA, LON_PAULISTA
        );
        assertEquals(0.0, distancia, 0.001,
                "Distância entre o mesmo ponto deve ser zero");
    }

    @Test
    @DisplayName("calcularDistancia: pontos com ~1km de distância real")
    void calcularDistancia_pontosConhecidos_retornaDistanciaCorreta() {
        // Av. Paulista → Parque Trianon (~900m em linha reta)
        double distancia = localizacaoService.calcularDistancia(
                -23.5631, -46.6544,
                -23.5726, -46.6567
        );
        // Tolerância de ±100m para arredondamentos de coordenadas
        assertTrue(distancia > 800 && distancia < 1200,
                "Distância esperada entre 800m e 1200m, obtida: " + distancia);
    }

    @Test
    @DisplayName("calcularDistancia: pontos em cidades diferentes retorna valor alto")
    void calcularDistancia_cidadesDiferentes_retornaDistanciaAlta() {
        // São Paulo → Rio de Janeiro (~360km)
        double distancia = localizacaoService.calcularDistancia(
                -23.5505, -46.6333,  // São Paulo
                -22.9068, -43.1729   // Rio de Janeiro
        );
        assertTrue(distancia > 300000,
                "Distância SP-RJ deve ser maior que 300km, obtida: " + distancia + "m");
    }

    // ===================================================================
    // estaNaZonaSegura
    // ===================================================================

    @Test
    @DisplayName("estaNaZonaSegura: sem zonas cadastradas deve liberar acesso")
    void estaNaZonaSegura_semZonas_liberaAcesso() {
        when(zonaSeguraRepository.findByUsuarioId(USUARIO_ID))
                .thenReturn(Collections.emptyList());

        boolean resultado = localizacaoService.estaNaZonaSegura(
                USUARIO_ID, LAT_PAULISTA, LON_PAULISTA
        );

        assertTrue(resultado,
                "Sem zonas cadastradas, acesso deve ser liberado");
        verify(zonaSeguraRepository).findByUsuarioId(USUARIO_ID);
    }

    @Test
    @DisplayName("estaNaZonaSegura: dentro do raio deve retornar true")
    void estaNaZonaSegura_dentroDoRaio_retornaTrue() {
        ZonaSegura zona = criarZona(LAT_PAULISTA, LON_PAULISTA, 500.0);
        when(zonaSeguraRepository.findByUsuarioId(USUARIO_ID))
                .thenReturn(List.of(zona));

        // Localização praticamente igual ao centro da zona
        boolean resultado = localizacaoService.estaNaZonaSegura(
                USUARIO_ID, LAT_PAULISTA + 0.001, LON_PAULISTA + 0.001
        );

        assertTrue(resultado, "Localização dentro do raio deve retornar true");
    }

    @Test
    @DisplayName("estaNaZonaSegura: fora do raio deve retornar false")
    void estaNaZonaSegura_foraDoRaio_retornaFalse() {
        ZonaSegura zona = criarZona(LAT_PAULISTA, LON_PAULISTA, 100.0);
        when(zonaSeguraRepository.findByUsuarioId(USUARIO_ID))
                .thenReturn(List.of(zona));

        // Coordenadas do Parque Trianon (~1km da Paulista)
        boolean resultado = localizacaoService.estaNaZonaSegura(
                USUARIO_ID, -23.5726, -46.6567
        );

        assertFalse(resultado, "Localização fora do raio deve retornar false");
    }

    @Test
    @DisplayName("estaNaZonaSegura: múltiplas zonas, dentro de uma deve retornar true")
    void estaNaZonaSegura_multipasZonas_retornaTrueSeEmQualquerUma() {
        ZonaSegura zonaLonge  = criarZona(-22.9068, -43.1729, 100.0); // Rio de Janeiro
        ZonaSegura zonaPerto  = criarZona(LAT_PAULISTA, LON_PAULISTA, 500.0); // Paulista
        when(zonaSeguraRepository.findByUsuarioId(USUARIO_ID))
                .thenReturn(List.of(zonaLonge, zonaPerto));

        boolean resultado = localizacaoService.estaNaZonaSegura(
                USUARIO_ID, LAT_PAULISTA, LON_PAULISTA
        );

        assertTrue(resultado,
                "Deve retornar true se localização está dentro de ao menos uma zona");
    }

    // -------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------
    private ZonaSegura criarZona(double lat, double lon, double raio) {
        ZonaSegura zona = new ZonaSegura();
        zona.setLatitude(lat);
        zona.setLongitude(lon);
        zona.setRaioMetros(raio);
        zona.setDescricao("Zona de teste");
        return zona;
    }
}
