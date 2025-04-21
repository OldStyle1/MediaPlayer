package com.example.Client.service;

import com.example.Client.model.AudioFile;
import com.example.Client.model.Playlist;
import com.example.Client.model.PlaylistItem;
import com.example.Client.model.User;
import com.example.Client.repository.AudioFileRepository;
import com.example.Client.repository.PlaylistItemRepository;
import com.example.Client.repository.PlaylistRepository;
import com.example.Client.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final AudioFileRepository audioFileRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, 
                          PlaylistItemRepository playlistItemRepository,
                          AudioFileRepository audioFileRepository,
                          UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistItemRepository = playlistItemRepository;
        this.audioFileRepository = audioFileRepository;
        this.userRepository = userRepository;
    }

    // Получить плейлист по ID
    public Playlist findById(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист с ID " + playlistId + " не найден!"));
    }
    
    // Создать новый плейлист
    @Transactional
    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }
    
    // Обновить плейлист
    @Transactional
    public Playlist updatePlaylist(Long id, Playlist playlist) {
        Playlist existingPlaylist = findById(id);
        
        existingPlaylist.setName(playlist.getName());
        existingPlaylist.setDescription(playlist.getDescription());
        existingPlaylist.setIsPublic(playlist.getIsPublic());
        
        return playlistRepository.save(existingPlaylist);
    }
    
    // Удалить плейлист
    @Transactional
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = findById(playlistId);
        playlistRepository.delete(playlist);
    }
    
    // Проверить, создан ли плейлист пользователем
    public boolean isCreatedBy(Long playlistId, String username) {
        Playlist playlist = findById(playlistId);
        return playlist.getCreatedBy().getUsername().equals(username);
    }
    
    // Добавить аудиофайл в плейлист
    @Transactional
    public PlaylistItem addAudioFileToPlaylist(Long audioFileId, Long playlistId) {
        Playlist playlist = findById(playlistId);
        AudioFile audioFile = audioFileRepository.findById(audioFileId)
                .orElseThrow(() -> new RuntimeException("Аудиофайл с ID " + audioFileId + " не найден!"));
        
        // Проверяем, есть ли уже этот файл в плейлисте
        Optional<PlaylistItem> existingItem = playlist.getItems().stream()
                .filter(item -> item.getAudioFile().getId().equals(audioFileId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            return existingItem.get(); // Файл уже в плейлисте
        }
        
        PlaylistItem item = new PlaylistItem();
        item.setAudioFile(audioFile);
        item.setPlaylist(playlist);
        item.setPosition(playlist.getItems().size());
        
        PlaylistItem savedItem = playlistItemRepository.save(item);
        playlist.getItems().add(savedItem);
        
        return savedItem;
    }
    
    // Удалить аудиофайл из плейлиста
    @Transactional
    public void removeAudioFileFromPlaylist(Long audioFileId, Long playlistId) {
        Playlist playlist = findById(playlistId);
        AudioFile audioFile = audioFileRepository.findById(audioFileId)
                .orElseThrow(() -> new RuntimeException("Аудиофайл с ID " + audioFileId + " не найден!"));
        
        PlaylistItem item = playlistItemRepository.findByPlaylistAndAudioFile(playlist, audioFile);
        
        if (item != null) {
            playlist.removeItem(item);
            playlistItemRepository.delete(item);
            
            // Пересортировка элементов
            List<PlaylistItem> items = playlistItemRepository.findByPlaylistOrderByPosition(playlist);
            for (int i = 0; i < items.size(); i++) {
                PlaylistItem currentItem = items.get(i);
                currentItem.setPosition(i);
                playlistItemRepository.save(currentItem);
            }
        }
    }
    
    // Получить элементы плейлиста
    public List<PlaylistItem> getPlaylistItems(Long playlistId) {
        Playlist playlist = findById(playlistId);
        return playlistItemRepository.findByPlaylistOrderByPosition(playlist);
    }
    
    // Получить все плейлисты
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }
    
    // Получить плейлисты пользователя
    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByCreatedById(userId);
    }
    
    // Изменить порядок элементов в плейлисте
    @Transactional
    public void reorderPlaylist(Long playlistId, List<Long> itemIds) {
        Playlist playlist = findById(playlistId);
        
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            PlaylistItem item = playlistItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Элемент плейлиста с ID " + itemId + " не найден!"));
            
            // Проверяем, принадлежит ли элемент этому плейлисту
            if (!item.getPlaylist().getId().equals(playlistId)) {
                throw new RuntimeException("Элемент плейлиста с ID " + itemId + " не принадлежит плейлисту с ID " + playlistId);
            }
            
            item.setPosition(i);
            playlistItemRepository.save(item);
        }
    }
    
    // Добавить плейлист в избранное
    @Transactional
    public void addToFavorites(Long playlistId, Long userId) {
        Playlist playlist = findById(playlistId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден!"));
        
        if (!playlist.getFavoritedBy().contains(user)) {
            playlist.addToFavorites(user);
            playlistRepository.save(playlist);
        }
    }
    
    // Удалить плейлист из избранного
    @Transactional
    public void removeFromFavorites(Long playlistId, Long userId) {
        Playlist playlist = findById(playlistId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден!"));
        
        playlist.removeFromFavorites(user);
        playlistRepository.save(playlist);
    }
    
    // Получить избранные плейлисты пользователя
    public List<Playlist> getFavoritePlaylists(Long userId) {
        return playlistRepository.findFavoritesByUserId(userId);
    }
    
    // Поиск плейлистов по имени
    public List<Playlist> searchPlaylists(String query) {
        return playlistRepository.findByNameContainingIgnoreCase(query);
    }
}
