package com.soulup.controller;

import com.soulup.dto.TreinoResponse;
import com.soulup.service.TreinoService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @PostMapping(value = "/analisar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TreinoResponse> analisar(
            @RequestParam Long usuarioId,
            @RequestParam MultipartFile video
    ) {
        return ResponseEntity.ok(treinoService.analisar(usuarioId, video));
    }

    @GetMapping
    public ResponseEntity<List<TreinoResponse>> listar() {
        return ResponseEntity.ok(treinoService.listar());
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<TreinoResponse>> listarPorUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.listarPorUsuario(id));
    }
}
