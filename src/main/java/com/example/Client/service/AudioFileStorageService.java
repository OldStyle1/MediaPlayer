package com.example.Client.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class AudioFileStorageService {
    private final String uploadDir = "uploads"; // Локальная директория для сохранения файлов

    public AudioFileStorageService() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // Создаём директорию, если она не существует
        }
    }

    // Загрузка файла
    public String uploadAudioFile(MultipartFile file) throws IOException {
        String filePath = uploadDir + File.separator + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest); // Сохраняем файл
        return filePath;
    }

    // Удаление файла
    public void deleteAudioFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
