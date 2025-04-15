package com.example.Client.service;

import com.example.Client.minio.MinIOService;
import com.example.Client.model.AudioFile;
import com.example.Client.model.User;
import com.example.Client.repository.AudioFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AudioFileService {

    private final AudioFileRepository audioFileRepository;
    private final MinIOService minIOService;

    public AudioFileService(AudioFileRepository audioFileRepository, MinIOService minIOService) {
        this.audioFileRepository = audioFileRepository;
        this.minIOService = minIOService;
    }

    // Загрузка аудиофайла (в MinIO и сохранение метаданных в БД)
    public AudioFile uploadAudioFile(MultipartFile file, User user) throws IOException {
        // Загружаем файл в MinIO и получаем его URL
        String fileUrl = minIOService.uploadFile(file);

        // Создаем объект AudioFile для хранения метаданных в БД
        AudioFile audioFile = new AudioFile();
        audioFile.setFileUrl(fileUrl);
        audioFile.setTitle(file.getOriginalFilename());
        audioFile.setUploadedBy(user);
        audioFile.setDuration(0); // Можете позже добавить обработку длительности файла, если потребуется

        // Сохраняем метаданные в базе данных
        return audioFileRepository.save(audioFile);
    }

    // Сохранение объекта AudioFile напрямую (только в базу)
    public AudioFile uploadAudioFile(AudioFile audioFile) {
        return audioFileRepository.save(audioFile);
    }

    // Удаление аудиофайла (из MinIO и базы данных)
    public void deleteAudioFile(Long audioFileId) {
        // Получаем аудиофайл из базы
        AudioFile audioFile = audioFileRepository.findById(audioFileId)
                .orElseThrow(() -> new RuntimeException("Аудиофайл не найден"));

        // Удаляем файл из MinIO
        minIOService.deleteFile(audioFile.getFileUrl());

        // Удаляем метаданные из базы
        audioFileRepository.delete(audioFile);
    }

    // Получение файла из MinIO через URL
    public String getAudioFileUrl(Long audioFileId) {
        // Получаем аудиофайл из базы
        AudioFile audioFile = audioFileRepository.findById(audioFileId)
                .orElseThrow(() -> new RuntimeException("Аудиофайл не найден"));

        // Возвращаем URL файла из MinIO
        return minIOService.getFileUrl(audioFile.getFileUrl());
    }

    // Получить все аудиофайлы (метаданные)
    public List<AudioFile> findAllAudioFiles() {
        return audioFileRepository.findAll();
    }

    // Найти аудиофайл по ID
    public AudioFile findById(Long id) {
        return audioFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Аудиофайл с id " + id + " не найден!"));
    }

    public List<AudioFile> findUserAudioFiles(Long userId) {
        return audioFileRepository.findByUploadedById(userId);
    }

    public AudioFile updateAudioFile(Long id, AudioFile audioFile) {
        AudioFile existingFile = audioFileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Audio file not found"));
        existingFile.setTitle(audioFile.getTitle());
        existingFile.setDescription(audioFile.getDescription());
        return audioFileRepository.save(existingFile);
    }
}
