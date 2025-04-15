package com.example.Client.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "playlist_items")
@Data
public class PlaylistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    private AudioFile audioFile;

    @Column(nullable = false)
    private Integer orderIndex;

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }
}
