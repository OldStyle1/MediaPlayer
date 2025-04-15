package com.example.Client.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "audio_files")
@Data
public class AudioFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // Имя файла

    @Column(length = 2000)
    private String description; // Описание аудиофайла

    private String fileUrl; // URL файла или путь в системе

    private Integer duration; // Продолжительность файла в секундах

    private LocalDateTime uploadDate; // Дата загрузки файла

    @OneToMany(mappedBy = "audioFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AudioFileTag> audioFileTags; // Связь с тегами

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy; // Пользователь, загрузивший файл

    private Integer playsCount = 0; // Счётчик воспроизведений

    @Column(length = 500)
    private String metadata; // Дополнительные данные в виде строки

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDateTime.now();
    }

    // Конструктор по умолчанию
    public AudioFile() {
    }

    public AudioFile(Long id, String fileName, String fileType, Long fileSize) {
        this.id = id;
        this.title = fileName;
        this.fileUrl = fileType;
        this.duration = Math.toIntExact(fileSize);
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
