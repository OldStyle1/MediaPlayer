package com.example.Client.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private Boolean isPublic = false;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistItem> items = new ArrayList<>();

    // Создаем связь с пользователями, добавившими плейлист в избранное
    @ManyToMany
    @JoinTable(
        name = "user_favorite_playlists",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> favoritedBy = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Методы для управления элементами плейлиста
    public void addItem(PlaylistItem item) {
        items.add(item);
        item.setPlaylist(this);
        // Устанавливаем позицию в конец списка
        item.setPosition(items.size() - 1);
    }
    
    public void removeItem(PlaylistItem item) {
        items.remove(item);
        item.setPlaylist(null);
        // Пересчитываем позиции
        reorderItems();
    }
    
    // Пересчет позиций элементов
    private void reorderItems() {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPosition(i);
        }
    }
    
    // Методы для управления избранным
    public void addToFavorites(User user) {
        favoritedBy.add(user);
    }
    
    public void removeFromFavorites(User user) {
        favoritedBy.remove(user);
    }

    public List<PlaylistItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setItems(List<PlaylistItem> items) {
        this.items = items;
    }

    public List<User> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(List<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }
}
