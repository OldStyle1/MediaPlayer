package com.example.Client.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audio_statistics")
public class AudioStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    private AudioFile audioFile;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Nullable, если пользователь не авторизован
    private User user;

    // Добавить необходимые поля
    private LocalDateTime playDate;
    private String ipAddress;
    private String userAgent;
    
    // Добавляем поле playCount
    private Integer playCount;
    
    @PrePersist
    protected void onCreate() {
        this.playDate = LocalDateTime.now();
    }
}
