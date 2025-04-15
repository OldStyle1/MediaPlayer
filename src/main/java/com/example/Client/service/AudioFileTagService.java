package com.example.Client.service;

import com.example.Client.model.AudioFile;
import com.example.Client.model.AudioFileTag;
import com.example.Client.model.Tag;
import com.example.Client.repository.AudioFileTagRepository;

public class AudioFileTagService {
    private final AudioFileTagRepository audioFileTagRepository;

    public AudioFileTagService(AudioFileTagRepository audioFileTagRepository) {
        this.audioFileTagRepository = audioFileTagRepository;
    }

    public AudioFileTag associateTagWithAudioFile(AudioFile audioFile, Tag tag) {
        AudioFileTag audioFileTag = new AudioFileTag();
        audioFileTag.setAudioFile(audioFile);
        audioFileTag.setTag(tag);
        return audioFileTagRepository.save(audioFileTag);
    }

    public void removeTagFromAudioFile(AudioFile audioFile, Tag tag) {
        AudioFileTag audioFileTag = audioFileTagRepository
                .findByAudioFileAndTag(audioFile, tag)
                .orElseThrow(() -> new RuntimeException("Связь между аудиофайлом и тегом не найдена!"));
        audioFileTagRepository.delete(audioFileTag);
    }
}
