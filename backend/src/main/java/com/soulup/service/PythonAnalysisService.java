package com.soulup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soulup.dto.VideoAnalysisResult;
import com.soulup.exception.VideoProcessingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PythonAnalysisService {

    private final ObjectMapper objectMapper;
    private final String pythonExecutable;
    private final Path scriptPath;
    private final Duration timeout;

    public PythonAnalysisService(
            ObjectMapper objectMapper,
            @Value("${soulup.python.executable}") String pythonExecutable,
            @Value("${soulup.python.script-path}") Path scriptPath,
            @Value("${soulup.video.processing-timeout-seconds}") long timeoutSeconds
    ) {
        this.objectMapper = objectMapper;
        this.pythonExecutable = pythonExecutable;
        this.scriptPath = scriptPath.toAbsolutePath().normalize();
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    public VideoAnalysisResult analyze(Path videoPath) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonExecutable,
                scriptPath.toString(),
                videoPath.toAbsolutePath().normalize().toString()
        );
        processBuilder.redirectErrorStream(false);

        try {
            Process process = processBuilder.start();
            CompletableFuture<String> stdoutFuture = readAsync(process.getInputStream());
            CompletableFuture<String> stderrFuture = readAsync(process.getErrorStream());

            boolean finished = process.waitFor(timeout.toSeconds(), TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new VideoProcessingException("Tempo limite excedido ao analisar o video.");
            }

            String stdout = stdoutFuture.join().trim();
            String stderr = stderrFuture.join().trim();

            if (process.exitValue() != 0) {
                throw new VideoProcessingException("Python retornou erro: " + stderr);
            }

            if (stdout.isBlank()) {
                throw new VideoProcessingException("Python nao retornou resultado da analise.");
            }

            return objectMapper.readValue(stdout, VideoAnalysisResult.class);
        } catch (IOException exception) {
            throw new VideoProcessingException("Nao foi possivel executar o analisador Python.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new VideoProcessingException("Processamento de video interrompido.", exception);
        }
    }

    private CompletableFuture<String> readAsync(java.io.InputStream inputStream) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                throw new VideoProcessingException("Falha ao ler saida do processo Python.", exception);
            }
        });
    }
}
