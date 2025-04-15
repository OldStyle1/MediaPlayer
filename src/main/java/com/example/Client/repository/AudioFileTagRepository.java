package com.example.Client.repository;

import com.example.Client.model.AudioFile;
import com.example.Client.model.AudioFileTag;
import com.example.Client.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AudioFileTagRepository extends JpaRepository<AudioFileTag, Long> {
    Optional<AudioFileTag> findByAudioFileAndTag(AudioFile audioFile, Tag tag);
}
