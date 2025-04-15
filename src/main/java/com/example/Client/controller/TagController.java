package com.example.Client.controller;

import com.example.Client.model.Tag;
import com.example.Client.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Теги")
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public Tag createTag(@RequestBody Tag tag) {
        return tagService.createTag(tag);
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/{name}")
    public Tag getTagByName(@PathVariable String name) {
        return tagService.findByName(name);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
    }

    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        return tagService.updateTag(id, tag);
    }

    @GetMapping("/audio/{audioId}")
    public List<Tag> getAudioFileTags(@PathVariable Long audioId) {
        return tagService.getAudioFileTags(audioId);
    }
}
