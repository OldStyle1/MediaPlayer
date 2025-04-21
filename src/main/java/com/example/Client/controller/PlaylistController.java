package com.example.Client.controller;

import com.example.Client.model.Playlist;
import com.example.Client.model.PlaylistItem;
import com.example.Client.model.User;
import com.example.Client.service.PlaylistService;
import com.example.Client.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Плейлисты", description = "API для работы с плейлистами")
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    
    private final PlaylistService playlistService;
    private final UserService userService;
    
    @Autowired
    public PlaylistController(PlaylistService playlistService, UserService userService) {
        this.playlistService = playlistService;
        this.userService = userService;
    }
    
    @Operation(summary = "Создать новый плейлист")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist) {
        User currentUser = userService.getCurrentUser();
        playlist.setCreatedBy(currentUser);
        Playlist createdPlaylist = playlistService.createPlaylist(playlist);
        return ResponseEntity.ok(createdPlaylist);
    }
    
    @Operation(summary = "Получить плейлист по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id) {
        Playlist playlist = playlistService.findById(id);
        return ResponseEntity.ok(playlist);
    }
    
    @Operation(summary = "Обновить плейлист")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PUBLISHER') and @playlistService.isCreatedBy(#id, principal.username))")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlist);
        return ResponseEntity.ok(updatedPlaylist);
    }
    
    @Operation(summary = "Удалить плейлист")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PUBLISHER') and @playlistService.isCreatedBy(#id, principal.username))")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Получить все плейлисты")
    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists);
    }
    
    @Operation(summary = "Получить плейлисты пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable Long userId) {
        List<Playlist> playlists = playlistService.getUserPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }
    
    @Operation(summary = "Получить элементы плейлиста")
    @GetMapping("/{id}/items")
    public ResponseEntity<List<PlaylistItem>> getPlaylistItems(@PathVariable Long id) {
        List<PlaylistItem> items = playlistService.getPlaylistItems(id);
        return ResponseEntity.ok(items);
    }
    
    @Operation(summary = "Добавить аудиофайл в плейлист")
    @PostMapping("/{playlistId}/items/{audioFileId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PUBLISHER') and @playlistService.isCreatedBy(#playlistId, principal.username))")
    public ResponseEntity<PlaylistItem> addAudioFileToPlaylist(
            @PathVariable Long playlistId, 
            @PathVariable Long audioFileId) {
        PlaylistItem item = playlistService.addAudioFileToPlaylist(audioFileId, playlistId);
        return ResponseEntity.ok(item);
    }
    
    @Operation(summary = "Удалить аудиофайл из плейлиста")
    @DeleteMapping("/{playlistId}/items/{audioFileId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PUBLISHER') and @playlistService.isCreatedBy(#playlistId, principal.username))")
    public ResponseEntity<Void> removeAudioFileFromPlaylist(
            @PathVariable Long playlistId, 
            @PathVariable Long audioFileId) {
        playlistService.removeAudioFileFromPlaylist(audioFileId, playlistId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Изменить порядок элементов в плейлисте")
    @PutMapping("/{id}/reorder")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PUBLISHER') and @playlistService.isCreatedBy(#id, principal.username))")
    public ResponseEntity<Void> reorderPlaylist(
            @PathVariable Long id, 
            @RequestBody List<Long> itemIds) {
        playlistService.reorderPlaylist(id, itemIds);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Добавить плейлист в избранное")
    @PostMapping("/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        playlistService.addToFavorites(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Удалить плейлист из избранного")
    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        playlistService.removeFromFavorites(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Получить избранные плейлисты")
    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Playlist>> getFavoritePlaylists() {
        User currentUser = userService.getCurrentUser();
        List<Playlist> playlists = playlistService.getFavoritePlaylists(currentUser.getId());
        return ResponseEntity.ok(playlists);
    }
    
    @Operation(summary = "Поиск плейлистов")
    @GetMapping("/search")
    public ResponseEntity<List<Playlist>> searchPlaylists(@RequestParam String query) {
        List<Playlist> playlists = playlistService.searchPlaylists(query);
        return ResponseEntity.ok(playlists);
    }
    
    @Operation(summary = "Мои плейлисты")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Playlist>> getMyPlaylists() {
        User currentUser = userService.getCurrentUser();
        List<Playlist> playlists = playlistService.getUserPlaylists(currentUser.getId());
        return ResponseEntity.ok(playlists);
    }
}