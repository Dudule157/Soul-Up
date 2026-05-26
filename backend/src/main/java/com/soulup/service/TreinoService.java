package com.soulup.service;

import com.soulup.dto.TreinoResponse;
import com.soulup.dto.VideoAnalysisResult;
import com.soulup.entity.Treino;
import com.soulup.entity.Usuario;
import com.soulup.exception.VideoProcessingException;
import com.soulup.repository.TreinoRepository;
import com.soulup.util.VideoFileValidator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final UsuarioService usuarioService;
    private final PythonAnalysisService pythonAnalysisService;
    private final Path uploadDir;

    public TreinoService(
            TreinoRepository treinoRepository,
            UsuarioService usuarioService,
            PythonAnalysisService pythonAnalysisService,
            @Value("${soulup.video.upload-dir}") Path uploadDir
    ) {
        this.treinoRepository = treinoRepository;
        this.usuarioService = usuarioService;
        this.pythonAnalysisService = pythonAnalysisService;
        this.uploadDir = uploadDir.toAbsolutePath().normalize();
    }

    @Transactional
    public TreinoResponse analisar(Long usuarioId, MultipartFile video) {
        VideoFileValidator.validate(video);
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        Path savedVideo = salvarVideoTemporario(video);

        VideoAnalysisResult result = pythonAnalysisService.analyze(savedVideo);
        Treino treino = new Treino(
                usuario,
                result.exercise(),
                result.repetitions(),
                result.duration(),
                result.feedback()
        );

        return TreinoResponse.fromEntity(treinoRepository.save(treino));
    }

    @Transactional(readOnly = true)
    public List<TreinoResponse> listar() {
        return treinoRepository.findAllByOrderByDataTreinoDesc()
                .stream()
                .map(TreinoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TreinoResponse> listarPorUsuario(Long usuarioId) {
        usuarioService.buscarEntidadePorId(usuarioId);
        return treinoRepository.findByUsuarioIdOrderByDataTreinoDesc(usuarioId)
                .stream()
                .map(TreinoResponse::fromEntity)
                .toList();
    }

    private Path salvarVideoTemporario(MultipartFile video) {
        try {
            Files.createDirectories(uploadDir);
            String extension = VideoFileValidator.getExtension(video.getOriginalFilename());
            Path target = uploadDir.resolve(UUID.randomUUID() + "." + extension).normalize();

            if (!target.startsWith(uploadDir)) {
                throw new VideoProcessingException("Caminho de upload invalido.");
            }

            video.transferTo(target);
            return target;
        } catch (IOException exception) {
            throw new VideoProcessingException("Nao foi possivel salvar o video recebido.", exception);
        }
    }
}
