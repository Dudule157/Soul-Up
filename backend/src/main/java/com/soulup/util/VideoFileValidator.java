package com.soulup.util;

import com.soulup.exception.BusinessException;
import java.util.Locale;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public final class VideoFileValidator {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp4", "mov", "avi");

    private VideoFileValidator() {
    }

    public static void validate(MultipartFile video) {
        if (video == null || video.isEmpty()) {
            throw new BusinessException("Video e obrigatorio.");
        }

        String originalFilename = video.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("Arquivo de video sem extensao valida.");
        }

        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("Formato de video nao suportado. Use mp4, mov ou avi.");
        }
    }

    public static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
