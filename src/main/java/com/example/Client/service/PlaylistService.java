package com.example.Client.service;

import com.example.Client.model.AudioFile;
import com.example.Client.model.Playlist;
import com.example.Client.model.PlaylistItem;
import com.example.Client.repository.PlaylistItemRepository;
import com.example.Client.repository.PlaylistRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;

    public PlaylistService(PlaylistRepository playlistRepository, PlaylistItemRepository playlistItemRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistItemRepository = playlistItemRepository;
    }

    // Найти плейлист по ID
    public Playlist findById(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист с ID " + playlistId + " не найден!"));
    }

    // Создать новый плейлист
    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    // Удалить плейлист (и все его элементы)
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден!"));
        playlistItemRepository.deleteAll(playlist.getItems()); // Удаляем все связанные элементы
        playlistRepository.delete(playlist);
    }

    // Добавить аудиофайл в плейлист
    public PlaylistItem addAudioFileToPlaylist(AudioFile audioFile, Playlist playlist) {
        PlaylistItem item = new PlaylistItem();
        item.setAudioFile(audioFile);
        item.setPlaylist(playlist);
        return playlistItemRepository.save(item);
    }

    // Удалить аудиофайл из плейлиста
    public void removeAudioFileFromPlaylist(AudioFile audioFile, Playlist playlist) {
        playlistItemRepository.deleteByAudioFileAndPlaylist(audioFile, playlist);
    }

    // Получить все аудиофайлы в плейлисте
    public List<PlaylistItem> getPlaylistItems(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден!"));
        return playlistItemRepository.findByPlaylist(playlist);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByCreatedById(userId);
    }

    public Playlist updatePlaylist(Long id, Playlist playlist) {
        Playlist existingPlaylist = playlistRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Playlist not found"));
        existingPlaylist.setName(playlist.getName());
        existingPlaylist.setDescription(playlist.getDescription());
        return playlistRepository.save(existingPlaylist);
    }

    public void reorderPlaylist(Long id, List<Long> itemIds) {
        // Реализация переупорядочивания элементов плейлиста
    }
}
