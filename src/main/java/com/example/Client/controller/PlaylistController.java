package com.example.Client.controller;

import com.example.Client.model.AudioFile;
import com.example.Client.model.Playlist;
import com.example.Client.model.PlaylistItem;
import com.example.Client.model.User;
import com.example.Client.service.PlaylistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Плейлисты")
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public Playlist createPlaylist(@RequestBody Playlist playlist) {
        // Получаем текущего аутентифицированного пользователя
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Устанавливаем создателя плейлиста
        playlist.setCreatedBy(currentUser);
        return playlistService.createPlaylist(playlist);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
    }

    @PostMapping("/{id}/add-audio")
    public PlaylistItem addAudioToPlaylist(@PathVariable Long id, @RequestBody AudioFile audioFile) {
        Playlist playlist = playlistService.findById(id);
        return playlistService.addAudioFileToPlaylist(audioFile, playlist);
    }

    @DeleteMapping("/{id}/remove-audio")
    public void removeAudioFromPlaylist(@PathVariable Long id, @RequestBody AudioFile audioFile) {
        Playlist playlist = playlistService.findById(id);
        playlistService.removeAudioFileFromPlaylist(audioFile, playlist);
    }

    @GetMapping("/{id}/items")
    public List<PlaylistItem> getPlaylistItems(@PathVariable Long id) {
        return playlistService.getPlaylistItems(id);
    }

    @GetMapping
    public List<Playlist> getAllPlaylists() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/user/{userId}")
    public List<Playlist> getUserPlaylists(@PathVariable Long userId) {
        return playlistService.getUserPlaylists(userId);
    }

    @PutMapping("/{id}")
    public Playlist updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        return playlistService.updatePlaylist(id, playlist);
    }

    @PutMapping("/{id}/reorder")
    public void reorderPlaylist(@PathVariable Long id, @RequestBody List<Long> itemIds) {
        playlistService.reorderPlaylist(id, itemIds);
    }
}