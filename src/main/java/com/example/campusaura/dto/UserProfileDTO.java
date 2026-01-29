package com.example.campusaura.dto;

public class UserProfileDTO {
    private String uid;
    private String email;
    private String name;
    private String userType;
    private Boolean isEmailVerified;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    
    // Student-specific fields
    private String degreeProgram;
    private String studentIdImageUrl;
    private Boolean isStudentVerified;

    public UserProfileDTO() {
    }

    public UserProfileDTO(String uid, String email, String name, String userType, Boolean isEmailVerified,
                          Boolean isActive, String createdAt, String updatedAt, String degreeProgram,
                          String studentIdImageUrl, Boolean isStudentVerified) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.isEmailVerified = isEmailVerified;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.degreeProgram = degreeProgram;
        this.studentIdImageUrl = studentIdImageUrl;
        this.isStudentVerified = isStudentVerified;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(String degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public String getStudentIdImageUrl() {
        return studentIdImageUrl;
    }

    public void setStudentIdImageUrl(String studentIdImageUrl) {
        this.studentIdImageUrl = studentIdImageUrl;
    }

    public Boolean getIsStudentVerified() {
        return isStudentVerified;
    }

    public void setIsStudentVerified(Boolean isStudentVerified) {
        this.isStudentVerified = isStudentVerified;
    }
}
