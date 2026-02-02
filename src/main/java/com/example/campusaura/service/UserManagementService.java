package com.example.campusaura.service;

import com.example.campusaura.dto.UserResponseDTO;
import com.example.campusaura.dto.UserStatsDTO;
import com.example.campusaura.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for admin user management operations.
 * Handles Firestore operations for user administration, verification, and statistics.
 */
@Service
public class UserManagementService {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "users";

    /**
     * Get all users
     */
    public List<UserResponseDTO> getAllUsers() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToUser)
                .map(this::userToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get users by type
     */
    public List<UserResponseDTO> getUsersByType(User.UserType userType) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("userType", userType.toString());
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToUser)
                .map(this::userToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending verification users
     */
    public List<UserResponseDTO> getPendingVerificationUsers() throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("verificationStatus", User.VerificationStatus.PENDING.toString());
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToUser)
                .map(this::userToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user statistics
     */
    public UserStatsDTO getUserStats() throws ExecutionException, InterruptedException {
        List<User> allUsers = getAllUsersInternal();
        
        long universityStudents = allUsers.stream()
                .filter(u -> u.getUserType() == User.UserType.UNIVERSITY_STUDENT)
                .count();
        
        long externalUsers = allUsers.stream()
                .filter(u -> u.getUserType() == User.UserType.EXTERNAL_USER)
                .count();
        
        long pendingVerification = allUsers.stream()
                .filter(u -> u.getVerificationStatus() == User.VerificationStatus.PENDING)
                .count();

        return new UserStatsDTO(universityStudents, externalUsers, pendingVerification);
    }

    /**
     * Update user status (active/inactive)
     */
    public UserResponseDTO updateUserStatus(String id, boolean active) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("active", active);
        updates.put("updatedAt", LocalDateTime.now().toString());

        docRef.update(updates).get();

        User user = documentToUser(docRef.get().get());
        return userToDTO(user);
    }

    /**
     * Verify university student
     */
    public UserResponseDTO verifyStudent(String id, User.VerificationStatus status) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("verificationStatus", status.toString());
        updates.put("updatedAt", LocalDateTime.now().toString());

        docRef.update(updates).get();

        User user = documentToUser(docRef.get().get());
        return userToDTO(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        docRef.delete().get();
    }

    /**
     * Helper methods
     */
    private List<User> getAllUsersInternal() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToUser)
                .collect(Collectors.toList());
    }

    private User documentToUser(DocumentSnapshot document) {
        User user = new User();
        user.setId(document.getId());
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setEmail(document.getString("email"));
        user.setPhoneNumber(document.getString("phoneNumber"));
        
        String userTypeStr = document.getString("userType");
        if (userTypeStr != null) {
            user.setUserType(User.UserType.valueOf(userTypeStr));
        }
        
        String verificationStatusStr = document.getString("verificationStatus");
        if (verificationStatusStr != null) {
            user.setVerificationStatus(User.VerificationStatus.valueOf(verificationStatusStr));
        }
        
        user.setIdImageUrl(document.getString("idImageUrl"));
        user.setStudentId(document.getString("studentId"));
        user.setActive(document.getBoolean("active") != null ? document.getBoolean("active") : true);
        
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null) {
            user.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }
        
        String updatedAtStr = document.getString("updatedAt");
        if (updatedAtStr != null) {
            user.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
        }
        
        return user;
    }

    private UserResponseDTO userToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setUserType(user.getUserType());
        dto.setVerificationStatus(user.getVerificationStatus());
        dto.setIdImageUrl(user.getIdImageUrl());
        dto.setStudentId(user.getStudentId());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
