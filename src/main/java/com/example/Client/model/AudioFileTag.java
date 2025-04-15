package com.example.Client.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "audio_file_tags")
@Data
public class AudioFileTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    private AudioFile audioFile;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }

    public void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
