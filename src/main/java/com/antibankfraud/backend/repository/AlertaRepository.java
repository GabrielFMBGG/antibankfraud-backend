package com.antibankfraud.backend.repository;

import com.antibankfraud.backend.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);
}