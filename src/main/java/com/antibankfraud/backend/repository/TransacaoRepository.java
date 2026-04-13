package com.antibankfraud.backend.repository;

import com.antibankfraud.backend.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);
}