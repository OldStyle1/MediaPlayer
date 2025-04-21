package com.example.Client.payload;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на аутентификацию")
public class LoginRequest {
    @Schema(description = "Имя пользователя", example = "user123")
    private String username;

    @Schema(description = "Пароль", example = "password123")
    private String password;

    // Геттеры и сеттеры
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 