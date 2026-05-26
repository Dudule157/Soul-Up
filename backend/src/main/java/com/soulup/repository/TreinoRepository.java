package com.soulup.repository;

import com.soulup.entity.Treino;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByUsuarioIdOrderByDataTreinoDesc(Long usuarioId);

    List<Treino> findAllByOrderByDataTreinoDesc();
}
