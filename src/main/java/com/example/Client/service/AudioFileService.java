package com.example.Client.service;

import com.example.Client.config.AwsConfig;
import com.example.Client.model.AudioFile;
import com.example.Client.model.AudioStatistic;
import com.example.Client.model.User;
import com.example.Client.repository.AudioFileRepository;
import com.example.Client.repository.AudioStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;

@Service
public class AudioFileService {
    private final AudioFileRepository audioFileRepository;
    private final AudioStatisticRepository audioStatisticRepository;
    private final AwsConfig awsConfig;

    @Value("${aws.bucket-name}")
    private String bucket;

    @Autowired
    public AudioFileService(AudioFileRepository audioFileRepository, 
                          AudioStatisticRepository audioStatisticRepository,
                          AwsConfig awsConfig) {
        this.audioFileRepository = audioFileRepository;
        this.audioStatisticRepository = audioStatisticRepository;
        this.awsConfig = awsConfig;
    }

    public AudioFile uploadAudioFile(MultipartFile audioFile, User user) throws IOException {
        // Генерируем уникальное имя файла
        String fileName = "audio/" + System.currentTimeMillis() + "_" + audioFile.getOriginalFilename();

        // Запрос на загрузку в S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(audioFile.getContentType())
                .build();

        awsConfig.s3Client().putObject(putObjectRequest, RequestBody.fromBytes(audioFile.getBytes()));

        // Сохраняем метаданные
        AudioFile fileEntity = new AudioFile();
        fileEntity.setTitle(audioFile.getOriginalFilename());
        fileEntity.setFileUrl(fileName);
        fileEntity.setUploadedBy(user);
        fileEntity.setDuration(null); // можно заменить после анализа аудио
        fileEntity.setDescription(null); // пользователь может заполнить позже
        fileEntity.setMetadata("contentType=" + audioFile.getContentType() + ";size=" + audioFile.getSize());

        return audioFileRepository.save(fileEntity);
    }

    public void deleteAudioFile(Long audioFileId) {

        AudioFile audioFile = findById(audioFileId);
        String fileKey = audioFile.getFileUrl();

        try {
            awsConfig.s3Client().deleteObject(builder ->
                    builder.bucket(bucket).key(fileKey).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Не удалось удалить файл из S3: " + e.getMessage());
        }

        audioFileRepository.delete(audioFile);
    }

    // Получить все аудиофайлы
    public List<AudioFile> findAllAudioFiles() {
        return audioFileRepository.findAll();
    }

    // Найти аудиофайл по ID
    public AudioFile findById(Long id) {
        return audioFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Аудиофайл с id " + id + " не найден!"));
    }

    // Найти аудиофайлы пользователя
    public List<AudioFile> findUserAudioFiles(Long userId) {
        return audioFileRepository.findByUploadedById(userId);
    }

    // Обновление аудиофайла
    public AudioFile updateAudioFile(Long id, AudioFile audioFile) {
        AudioFile existingFile = audioFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio file not found"));
        existingFile.setTitle(audioFile.getTitle());
        existingFile.setDescription(audioFile.getDescription());
        return audioFileRepository.save(existingFile);
    }

    public byte[] downloadAudioFile(String key) throws IOException {
        var getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        var inputStream = awsConfig.s3Client().getObject(getRequest);
        return inputStream.readAllBytes(); // читаем весь файл в массив байтов
    }

    // Проверка, загружен ли файл указанным пользователем
    public boolean isUploadedBy(Long audioFileId, String username) {
        AudioFile audioFile = findById(audioFileId);
        return audioFile.getUploadedBy().getUsername().equals(username);
    }
    
    // Запись статистики прослушивания
    public void recordPlayStatistic(Long audioFileId, User user) {
        AudioFile audioFile = findById(audioFileId);
        
        AudioStatistic statistic = new AudioStatistic();
        statistic.setAudioFile(audioFile);
        statistic.setUser(user);
        
        audioStatisticRepository.save(statistic);
        
        // Увеличиваем счетчик прослушиваний
        audioFile.setPlaysCount(audioFile.getPlaysCount() + 1);
        audioFileRepository.save(audioFile);
    }

}