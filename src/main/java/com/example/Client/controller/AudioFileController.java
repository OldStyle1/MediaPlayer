package com.example.Client.controller;

import com.example.Client.model.AudioFile;
import com.example.Client.model.User;
import com.example.Client.service.AudioFileService;
import com.example.Client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public AudioFile uploadAudioFile(@RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = getCurrentUser();
        return audioFileService.uploadAudioFile(file, currentUser);
    }

    @GetMapping
    public List<AudioFile> getAllAudioFiles() {
        return audioFileService.findAllAudioFiles();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadAudio(@RequestParam String key) throws IOException {
        byte[] data = audioFileService.downloadAudioFile(key);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"");

        return ResponseEntity.ok().headers(headers).body(data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('PUBLISHER') and @audioFileService.isUploadedBy(#id, principal.username))")
    public void deleteAudioFile(@PathVariable Long id) {
        audioFileService.deleteAudioFile(id);
    }

    @GetMapping("/user/{userId}")
    public List<AudioFile> getUserAudioFiles(@PathVariable Long userId) {
        return audioFileService.findUserAudioFiles(userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or (hasRole('PUBLISHER') and @audioFileService.isUploadedBy(#id, principal.username))")
    public AudioFile updateAudioFile(@PathVariable Long id, @RequestBody AudioFile audioFile) {
        return audioFileService.updateAudioFile(id, audioFile);
    }
    
    @PostMapping("/{id}/play")
    @PreAuthorize("isAuthenticated()")
    public void playAudioFile(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        audioFileService.recordPlayStatistic(id, currentUser);
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
