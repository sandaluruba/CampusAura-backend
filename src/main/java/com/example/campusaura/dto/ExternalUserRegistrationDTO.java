package com.example.campusaura.dto;

public class ExternalUserRegistrationDTO {
    private String uid;
    private String email;
    private String name;

    public ExternalUserRegistrationDTO() {
    }

    public ExternalUserRegistrationDTO(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
