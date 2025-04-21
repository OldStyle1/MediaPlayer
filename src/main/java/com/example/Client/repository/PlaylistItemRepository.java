package com.example.Client.repository;

import com.example.Client.model.AudioFile;
import com.example.Client.model.Playlist;
import com.example.Client.model.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {
    // Найти элементы плейлиста с сортировкой по позиции
    List<PlaylistItem> findByPlaylistOrderByPosition(Playlist playlist);
    
    // Найти элемент по плейлисту и аудиофайлу
    PlaylistItem findByPlaylistAndAudioFile(Playlist playlist, AudioFile audioFile);
    
    // Удалить элемент по плейлисту и аудиофайлу
    void deleteByPlaylistAndAudioFile(Playlist playlist, AudioFile audioFile);
    
    // Обновить позиции элементов
    @Modifying
    @Query("UPDATE PlaylistItem pi SET pi.position = :position WHERE pi.id = :id")
    void updatePosition(@Param("id") Long id, @Param("position") Integer position);
}
