package com.example.Client.repository;

import com.example.Client.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    // Найти плейлисты по создателю
    List<Playlist> findByCreatedById(Long userId);
    
    // Найти публичные плейлисты
    List<Playlist> findByIsPublicTrue();
    
    // Найти плейлисты в избранном у пользователя
    @Query("SELECT p FROM Playlist p JOIN p.favoritedBy u WHERE u.id = :userId")
    List<Playlist> findFavoritesByUserId(@Param("userId") Long userId);
    
    // Поиск плейлистов по имени (частичное совпадение)
    List<Playlist> findByNameContainingIgnoreCase(String name);
}
