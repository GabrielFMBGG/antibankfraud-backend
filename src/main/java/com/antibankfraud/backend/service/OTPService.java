package com.antibankfraud.backend.service;

import com.antibankfraud.backend.entity.OTP;
import com.antibankfraud.backend.entity.Usuario;
import com.antibankfraud.backend.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void gerarEEnviar(Usuario usuario) {
        // Gera código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // Salva no banco com expiração de 10 minutos
        OTP otp = new OTP();
        otp.setUsuario(usuario);
        otp.setCodigo(codigo);
        otp.setExpiracao(LocalDateTime.now().plusMinutes(10));
        otp.setUtilizado(false);
        otp.setTipo("EMAIL");
        otpRepository.save(otp);

        // Envia por email
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(usuario.getEmailSecundario() != null
                ? usuario.getEmailSecundario()
                : usuario.getEmail());
        mensagem.setSubject("Código de acesso de emergência - AntiBankFraud");
        mensagem.setText("Seu código de acesso é: " + codigo
                + "\n\nEste código expira em 10 minutos."
                + "\n\nSe você não solicitou este código, ignore este email.");
        mailSender.send(mensagem);
    }

    public boolean validar(Long usuarioId, String codigo) {
        Optional<OTP> otpOpt = otpRepository
                .findByUsuarioIdAndCodigoAndUtilizadoFalse(usuarioId, codigo);

        if (otpOpt.isEmpty()) return false;

        OTP otp = otpOpt.get();

        // Verifica se não expirou
        if (otp.getExpiracao().isBefore(LocalDateTime.now())) return false;

        // Marca como utilizado
        otp.setUtilizado(true);
        otpRepository.save(otp);
        return true;
    }
}