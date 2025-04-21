package com.example.Client.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playlist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    @JsonIgnore
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "audio_file_id", nullable = false)
    private AudioFile audioFile;

    private Integer position; // Позиция в плейлисте
    
    // Геттеры и сеттеры генерируются Lombok через аннотации @Getter и @Setter

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public AudioFile getAudioFile() {
        return audioFile;
    }
}
