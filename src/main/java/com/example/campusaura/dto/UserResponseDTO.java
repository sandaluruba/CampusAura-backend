package com.example.campusaura.dto;

import com.example.campusaura.model.User;

public class UserResponseDTO {
    private String message;
    private User user;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String message, User user) {
        this.message = message;
        this.user = user;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
