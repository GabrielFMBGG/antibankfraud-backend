package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.OTP;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.repository.OTPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OTPService — geração e validação de códigos de emergência")
class OTPServiceTest {

    @Mock private OTPRepository  otpRepository;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private OTPService otpService;

    private Usuario usuario;
    private OTP     otp;

    private static final Long   USUARIO_ID = 1L;
    private static final String CODIGO     = "123456";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(USUARIO_ID);
        usuario.setEmail("joao@email.com");
        usuario.setEmailSecundario("joao.sec@email.com");

        otp = new OTP();
        otp.setId(1L);
        otp.setUsuario(usuario);
        otp.setCodigo(CODIGO);
        otp.setUtilizado(false);
        otp.setExpiracao(LocalDateTime.now().plusMinutes(10));
    }

    // ===================================================================
    // validar()
    // ===================================================================

    @Test
    @DisplayName("validar: código correto e não expirado deve retornar true e marcar como utilizado")
    void validar_codigoValidoNaoExpirado_retornaTrue() {
        when(otpRepository.findByUsuarioIdAndCodigoAndUtilizadoFalse(USUARIO_ID, CODIGO))
                .thenReturn(Optional.of(otp));
        when(otpRepository.save(any(OTP.class))).thenReturn(otp);

        boolean resultado = otpService.validar(USUARIO_ID, CODIGO);

        assertTrue(resultado, "Código válido deve retornar true");
        assertTrue(otp.isUtilizado(), "OTP deve ser marcado como utilizado após validação");
        verify(otpRepository).save(otp);
    }

    @Test
    @DisplayName("validar: código não encontrado deve retornar false")
    void validar_codigoNaoEncontrado_retornaFalse() {
        when(otpRepository.findByUsuarioIdAndCodigoAndUtilizadoFalse(USUARIO_ID, "000000"))
                .thenReturn(Optional.empty());

        boolean resultado = otpService.validar(USUARIO_ID, "000000");

        assertFalse(resultado, "Código inexistente deve retornar false");
        verify(otpRepository, never()).save(any());
    }

    @Test
    @DisplayName("validar: código expirado deve retornar false")
    void validar_codigoExpirado_retornaFalse() {
        otp.setExpiracao(LocalDateTime.now().minusMinutes(5)); // já expirou
        when(otpRepository.findByUsuarioIdAndCodigoAndUtilizadoFalse(USUARIO_ID, CODIGO))
                .thenReturn(Optional.of(otp));

        boolean resultado = otpService.validar(USUARIO_ID, CODIGO);

        assertFalse(resultado, "Código expirado deve retornar false");
        assertFalse(otp.isUtilizado(), "OTP expirado não deve ser marcado como utilizado");
        verify(otpRepository, never()).save(any());
    }

    // ===================================================================
    // gerarEEnviar()
    // ===================================================================

    @Test
    @DisplayName("gerarEEnviar: deve salvar OTP e chamar mailSender")
    void gerarEEnviar_deveGerarSalvarEEnviarEmail() {
        when(otpRepository.save(any(OTP.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        otpService.gerarEEnviar(usuario);

        verify(otpRepository).save(argThat(o ->
                o.getUsuario().equals(usuario)
                && o.getCodigo() != null
                && o.getCodigo().length() == 6
                && !o.isUtilizado()
                && o.getExpiracao().isAfter(LocalDateTime.now())
        ));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
