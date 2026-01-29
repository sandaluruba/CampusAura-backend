package com.example.campusaura.service;

import com.example.campusaura.dto.ExternalUserRegistrationDTO;
import com.example.campusaura.dto.StudentRegistrationDTO;
import com.example.campusaura.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String COLLECTION_NAME = "users";
    private static final String STUDENT_EMAIL_DOMAIN = "@std.uwu.ac.lk";

    @Autowired
    private Firestore firestore;

    /**
     * Register a new student user
     */
    public User registerStudent(StudentRegistrationDTO dto) throws ExecutionException, InterruptedException {
        // Validate student email
        if (dto.getEmail() == null || !dto.getEmail().endsWith(STUDENT_EMAIL_DOMAIN)) {
            throw new IllegalArgumentException("Student email must end with " + STUDENT_EMAIL_DOMAIN);
        }

        // Validate required fields
        if (dto.getUid() == null || dto.getUid().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getDegreeProgram() == null || dto.getDegreeProgram().isEmpty()) {
            throw new IllegalArgumentException("Degree program is required");
        }

        // Check if user already exists
        DocumentSnapshot existingUser = firestore.collection(COLLECTION_NAME)
                .document(dto.getUid())
                .get()
                .get();
        
        if (existingUser.exists()) {
            throw new IllegalArgumentException("User already exists with UID: " + dto.getUid());
        }

        String timestamp = Instant.now().toString();

        // Create User object
        User user = new User();
        user.setUid(dto.getUid());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setUserType("STUDENT");
        user.setDegreeProgram(dto.getDegreeProgram());
        user.setStudentIdImageUrl(dto.getStudentIdImageUrl());
        user.setIsStudentVerified(false);
        user.setIsEmailVerified(false);
        user.setIsActive(true);
        user.setCreatedAt(timestamp);
        user.setUpdatedAt(timestamp);

        // Save to Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(dto.getUid())
                .set(user.toMap());
        result.get(); // Wait for completion

        return user;
    }

    /**
     * Register a new external user
     */
    public User registerExternalUser(ExternalUserRegistrationDTO dto) throws ExecutionException, InterruptedException {
        // Validate required fields
        if (dto.getUid() == null || dto.getUid().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        // Check if user already exists
        DocumentSnapshot existingUser = firestore.collection(COLLECTION_NAME)
                .document(dto.getUid())
                .get()
                .get();
        
        if (existingUser.exists()) {
            throw new IllegalArgumentException("User already exists with UID: " + dto.getUid());
        }

        String timestamp = Instant.now().toString();

        // Create User object
        User user = new User();
        user.setUid(dto.getUid());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setUserType("EXTERNAL");
        user.setIsEmailVerified(false);
        user.setIsActive(true);
        user.setCreatedAt(timestamp);
        user.setUpdatedAt(timestamp);

        // Save to Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(dto.getUid())
                .set(user.toMap());
        result.get(); // Wait for completion

        return user;
    }

    /**
     * Get user by UID
     */
    public User getUserByUid(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                .document(uid)
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        return convertMapToUser(document.getData());
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(String uid, Map<String, Object> updates) throws ExecutionException, InterruptedException {
        // Check if user exists
        User existingUser = getUserByUid(uid);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with UID: " + uid);
        }

        // Add updated timestamp
        updates.put("updatedAt", Instant.now().toString());

        // Prevent updating sensitive fields
        updates.remove("uid");
        updates.remove("userType");
        updates.remove("isStudentVerified");
        updates.remove("createdAt");

        // Update in Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(uid)
                .update(updates);
        result.get(); // Wait for completion

        // Return updated user
        return getUserByUid(uid);
    }

    /**
     * Verify student ID (admin function)
     */
    public boolean verifyStudentId(String uid, boolean isVerified) throws ExecutionException, InterruptedException {
        // Check if user exists
        User user = getUserByUid(uid);
        if (user == null) {
            throw new IllegalArgumentException("User not found with UID: " + uid);
        }

        // Verify user is a student
        if (!"STUDENT".equals(user.getUserType())) {
            throw new IllegalArgumentException("User is not a student");
        }

        // Update verification status
        Map<String, Object> updates = new HashMap<>();
        updates.put("isStudentVerified", isVerified);
        updates.put("updatedAt", Instant.now().toString());

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(uid)
                .update(updates);
        result.get(); // Wait for completion

        return true;
    }

    /**
     * Get all unverified students
     */
    public List<User> getAllUnverifiedStudents() throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userType", "STUDENT")
                .whereEqualTo("isStudentVerified", false);

        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

        return documents.stream()
                .map(doc -> convertMapToUser(doc.getData()))
                .collect(Collectors.toList());
    }

    /**
     * Delete user
     */
    public boolean deleteUser(String uid) throws ExecutionException, InterruptedException {
        // Check if user exists
        User user = getUserByUid(uid);
        if (user == null) {
            throw new IllegalArgumentException("User not found with UID: " + uid);
        }

        // Delete from Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(uid)
                .delete();
        result.get(); // Wait for completion

        return true;
    }

    /**
     * Convert Firestore Map to User object
     */
    private User convertMapToUser(Map<String, Object> data) {
        User user = new User();
        user.setUid((String) data.get("uid"));
        user.setEmail((String) data.get("email"));
        user.setName((String) data.get("name"));
        user.setUserType((String) data.get("userType"));
        user.setIsEmailVerified((Boolean) data.get("isEmailVerified"));
        user.setIsActive((Boolean) data.get("isActive"));
        
        // Handle timestamps - convert from Firestore Timestamp to String if needed
        Object createdAt = data.get("createdAt");
        if (createdAt instanceof com.google.cloud.Timestamp) {
            user.setCreatedAt(((com.google.cloud.Timestamp) createdAt).toDate().toInstant().toString());
        } else if (createdAt instanceof String) {
            user.setCreatedAt((String) createdAt);
        }
        
        Object updatedAt = data.get("updatedAt");
        if (updatedAt instanceof com.google.cloud.Timestamp) {
            user.setUpdatedAt(((com.google.cloud.Timestamp) updatedAt).toDate().toInstant().toString());
        } else if (updatedAt instanceof String) {
            user.setUpdatedAt((String) updatedAt);
        }
        
        // Student-specific fields
        if ("STUDENT".equals(user.getUserType())) {
            user.setDegreeProgram((String) data.get("degreeProgram"));
            user.setStudentIdImageUrl((String) data.get("studentIdImageUrl"));
            user.setIsStudentVerified((Boolean) data.get("isStudentVerified"));
        }
        
        return user;
    }
}
