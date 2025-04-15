package com.example.Client.repository;

import com.example.Client.model.AudioFile;
import com.example.Client.model.Playlist;
import com.example.Client.model.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {
    List<PlaylistItem> findByPlaylist(Playlist playlist);
    void deleteByAudioFileAndPlaylist(AudioFile audioFile, Playlist playlist);
}
