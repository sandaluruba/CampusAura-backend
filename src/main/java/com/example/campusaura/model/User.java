package com.example.campusaura.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserType userType; // UNIVERSITY_STUDENT, EXTERNAL_USER
    private VerificationStatus verificationStatus; // PENDING, VERIFIED, REJECTED
    private String idImageUrl; // URL to uploaded ID image
    private String studentId; // For university students
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums
    public enum UserType {
        UNIVERSITY_STUDENT,
        EXTERNAL_USER
    }

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }

    // Constructors
    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getIdImageUrl() {
        return idImageUrl;
    }

    public void setIdImageUrl(String idImageUrl) {
        this.idImageUrl = idImageUrl;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
}
