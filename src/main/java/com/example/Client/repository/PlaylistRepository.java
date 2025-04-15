package com.example.Client.repository;

import com.example.Client.model.AudioFile;
import com.example.Client.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByCreatedById(Long userId);
    
    // Поиск по названию
    List<Playlist> findByNameContainingIgnoreCase(String name);
    
    // Публичные плейлисты
    List<Playlist> findByIsPublicTrue();
    

}
