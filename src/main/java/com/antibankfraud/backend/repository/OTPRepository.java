package com.antibankfraud.backend.repository;

import com.antibankfraud.backend.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByUsuarioIdAndCodigoAndUtilizadoFalse(Long usuarioId, String codigo);
    void deleteByUsuarioId(Long usuarioId);
}