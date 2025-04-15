package com.example.Client.controller;

import com.example.Client.model.AudioFile;
import com.example.Client.model.User;
import com.example.Client.service.AudioFileService;
import com.example.Client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;

@Tag(name = "Audio Files", description = "API для работы с аудиофайлами")
@RestController
@RequestMapping("/api/audio-files")
public class AudioFileController {

    private final AudioFileService audioFileService;
    @Autowired
    private UserService userService;

    public AudioFileController(AudioFileService audioFileService) {
        this.audioFileService = audioFileService;
    }

    // Загрузка файла
    @PostMapping("/upload")
    public AudioFile uploadAudioFile(@RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = getCurrentUser(); // Предполагаем, что пользователь берется из контекста
        return audioFileService.uploadAudioFile(file, currentUser);
    }

    // Получение всех файлов
    @GetMapping
    public List<AudioFile> getAllAudioFiles() {
        return audioFileService.findAllAudioFiles();
    }

    // Удаление файла
    @DeleteMapping("/{id}")
    public void deleteAudioFile(@PathVariable Long id) {
        audioFileService.deleteAudioFile(id);
    }

    // Получение URL файла
    @GetMapping("/{id}/url")
    public String getAudioFileUrl(@PathVariable Long id) {
        return audioFileService.getAudioFileUrl(id);
    }

    // Получение файлов пользователя
    @GetMapping("/user/{userId}")
    public List<AudioFile> getUserAudioFiles(@PathVariable Long userId) {
        return audioFileService.findUserAudioFiles(userId);
    }

    // Обновление файла
    @PutMapping("/{id}")
    public AudioFile updateAudioFile(@PathVariable Long id, @RequestBody AudioFile audioFile) {
        return audioFileService.updateAudioFile(id, audioFile);
    }

    // Заменить временную заглушку getCurrentUser() на использование Spring Security
    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
