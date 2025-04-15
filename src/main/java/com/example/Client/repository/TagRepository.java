package com.example.Client.repository;

import com.example.Client.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    
    @Query("SELECT t FROM Tag t JOIN t.audioFileTags aft WHERE aft.audioFile.id = :audioId")
    List<Tag> findByAudioFileId(Long audioId);
    
    // Поиск по частичному совпадению
    List<Tag> findByNameContainingIgnoreCase(String name);
    
    // Популярные теги
    @Query("SELECT t FROM Tag t GROUP BY t.id ORDER BY COUNT(t.audioFileTags) DESC")
    List<Tag> findMostUsedTags(Pageable pageable);
}
