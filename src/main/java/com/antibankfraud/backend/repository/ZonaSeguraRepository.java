package com.antibankfraud.backend.repository;

import com.antibankfraud.backend.entity.ZonaSegura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaSeguraRepository extends JpaRepository<ZonaSegura, Long> {
    List<ZonaSegura> findByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}