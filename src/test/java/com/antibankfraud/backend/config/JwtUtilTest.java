package com.antibankfraud.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil — geração, extração e validação de tokens JWT")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Secret com pelo menos 32 chars para HMAC-SHA256
    private static final String SECRET     = "chave-secreta-super-longa-para-testes-unitarios-256bits";
    private static final Long   EXPIRATION = 86400000L; // 24h em ms
    private static final String EMAIL      = "joao@email.com";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Injeta os @Value sem subir o contexto Spring
        ReflectionTestUtils.setField(jwtUtil, "secret",     SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("gerarToken: deve retornar token JWT não nulo e não vazio")
    void gerarToken_retornaTokenValido() {
        String token = jwtUtil.gerarToken(EMAIL);

        assertNotNull(token, "Token não deve ser nulo");
        assertFalse(token.isBlank(), "Token não deve ser vazio");
        // JWT tem exatamente 3 partes separadas por ponto
        assertEquals(3, token.split("\\.").length,
                "Token JWT deve ter 3 partes (header.payload.signature)");
    }

    @Test
    @DisplayName("extrairEmail: token gerado deve conter o e-mail correto no payload")
    void extrairEmail_tokenValido_retornaEmailCorreto() {
        String token = jwtUtil.gerarToken(EMAIL);

        String emailExtraido = jwtUtil.extrairEmail(token);

        assertEquals(EMAIL, emailExtraido,
                "E-mail extraído deve ser igual ao e-mail usado na geração");
    }

    @Test
    @DisplayName("validarToken: token recém-gerado deve ser considerado válido")
    void validarToken_tokenRecemGerado_retornaTrue() {
        String token = jwtUtil.gerarToken(EMAIL);

        assertTrue(jwtUtil.validarToken(token),
                "Token recém-gerado deve ser válido");
    }

    @Test
    @DisplayName("validarToken: token malformado deve retornar false")
    void validarToken_tokenInvalido_retornaFalse() {
        assertFalse(jwtUtil.validarToken("token.invalido.aqui"),
                "Token malformado deve retornar false");
    }

    @Test
    @DisplayName("validarToken: token com assinatura adulterada deve retornar false")
    void validarToken_assinaturaAdulterada_retornaFalse() {
        String token = jwtUtil.gerarToken(EMAIL);
        // Adultera a assinatura (última parte)
        String[] partes = token.split("\\.");
        String tokenAdulterado = partes[0] + "." + partes[1] + ".assinaturaFalsa";

        assertFalse(jwtUtil.validarToken(tokenAdulterado),
                "Token com assinatura adulterada deve retornar false");
    }
}
