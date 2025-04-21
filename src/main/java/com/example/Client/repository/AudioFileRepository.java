package com.example.Client.repository;

import com.example.Client.model.AudioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
    @Override
    Optional<AudioFile> findById(Long aLong);

    List<AudioFile> findByUploadedById(Long userId);

    List<AudioFile> findByTitleContainingIgnoreCase(String title);

    List<AudioFile> findByUploadDateBetween(LocalDateTime start, LocalDateTime end);
}
