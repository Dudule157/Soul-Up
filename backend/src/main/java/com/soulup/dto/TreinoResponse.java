package com.soulup.dto;

import com.soulup.entity.Treino;
import java.time.LocalDateTime;

public record TreinoResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        String exercicio,
        Integer repeticoes,
        Integer duracao,
        String feedback,
        LocalDateTime dataTreino
) {
    public static TreinoResponse fromEntity(Treino treino) {
        return new TreinoResponse(
                treino.getId(),
                treino.getUsuario().getId(),
                treino.getUsuario().getNome(),
                treino.getExercicio(),
                treino.getRepeticoes(),
                treino.getDuracao(),
                treino.getFeedback(),
                treino.getDataTreino()
        );
    }
}
