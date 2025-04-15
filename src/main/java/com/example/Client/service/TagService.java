package com.example.Client.service;

import com.example.Client.model.Tag;
import com.example.Client.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createTag(Tag tag){
        if (tagRepository.findByName(tag.getName()).isPresent()) {
            throw new RuntimeException("Тег с таким именем уже существует!");
        }
        return tagRepository.save(tag);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Тег не найден!"));
    }

    public Tag updateTag(Long id, Tag tag) {
        Tag existingTag = tagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        existingTag.setName(tag.getName());
        return tagRepository.save(existingTag);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public List<Tag> getAudioFileTags(Long audioId) {
        return tagRepository.findByAudioFileId(audioId);
    }
}
