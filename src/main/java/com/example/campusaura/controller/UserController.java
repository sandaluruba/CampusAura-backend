package com.example.campusaura.controller;

import com.example.campusaura.dto.ExternalUserRegistrationDTO;
import com.example.campusaura.dto.StudentRegistrationDTO;
import com.example.campusaura.dto.UserResponseDTO;
import com.example.campusaura.model.User;
import com.example.campusaura.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Register a student user (Public endpoint)
     * POST /api/public/users/register/student
     */
    @PostMapping("/api/public/users/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegistrationDTO dto) {
        try {
            User user = userService.registerStudent(dto);
            UserResponseDTO response = new UserResponseDTO("Student registered successfully", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to register student: " + e.getMessage()));
        }
    }

    /**
     * Register an external user (Public endpoint)
     * POST /api/public/users/register/external
     */
    @PostMapping("/api/public/users/register/external")
    public ResponseEntity<?> registerExternalUser(@RequestBody ExternalUserRegistrationDTO dto) {
        try {
            User user = userService.registerExternalUser(dto);
            UserResponseDTO response = new UserResponseDTO("External user registered successfully", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to register external user: " + e.getMessage()));
        }
    }

    /**
     * Get current user profile (Protected endpoint)
     * GET /api/users/profile
     */
    @GetMapping("/api/users/profile")
    public ResponseEntity<?> getCurrentUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String uid = extractUserIdFromToken(authHeader);
            User user = userService.getUserByUid(uid);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("User not found"));
            }
            
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve user profile: " + e.getMessage()));
        }
    }

    /**
     * Update current user profile (Protected endpoint)
     * PUT /api/users/profile
     */
    @PutMapping("/api/users/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> updates) {
        try {
            String uid = extractUserIdFromToken(authHeader);
            User updatedUser = userService.updateUserProfile(uid, updates);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }

    /**
     * Delete current user account (Protected endpoint)
     * DELETE /api/users/profile
     */
    @DeleteMapping("/api/users/profile")
    public ResponseEntity<?> deleteCurrentUserAccount(@RequestHeader("Authorization") String authHeader) {
        try {
            String uid = extractUserIdFromToken(authHeader);
            userService.deleteUser(uid);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User account deleted successfully");
            response.put("uid", uid);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete account: " + e.getMessage()));
        }
    }

    /**
     * Get all unverified students (Admin endpoint)
     * GET /api/users/students/unverified
     */
    @GetMapping("/api/users/students/unverified")
    public ResponseEntity<?> getUnverifiedStudents(@RequestHeader("Authorization") String authHeader) {
        try {
            // Verify user is authenticated
            extractUserIdFromToken(authHeader);
            
            List<User> unverifiedStudents = userService.getAllUnverifiedStudents();
            return ResponseEntity.ok(unverifiedStudents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve unverified students: " + e.getMessage()));
        }
    }

    /**
     * Verify student ID (Admin endpoint)
     * PATCH /api/users/students/{uid}/verify
     */
    @PatchMapping("/api/users/students/{uid}/verify")
    public ResponseEntity<?> verifyStudentId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String uid,
            @RequestBody Map<String, Boolean> verificationData) {
        try {
            // Verify admin is authenticated
            extractUserIdFromToken(authHeader);
            
            Boolean isVerified = verificationData.get("isVerified");
            if (isVerified == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("isVerified field is required"));
            }
            
            userService.verifyStudentId(uid, isVerified);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student verification status updated successfully");
            response.put("uid", uid);
            response.put("isVerified", String.valueOf(isVerified));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to verify student: " + e.getMessage()));
        }
    }

    /**
     * Get any user by UID (Admin endpoint)
     * GET /api/users/{uid}
     */
    @GetMapping("/api/users/{uid}")
    public ResponseEntity<?> getUserByUid(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String uid) {
        try {
            // Verify admin is authenticated
            extractUserIdFromToken(authHeader);
            
            User user = userService.getUserByUid(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("User not found with UID: " + uid));
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve user: " + e.getMessage()));
        }
    }

    /**
     * Extract user ID from Firebase authentication token
     */
    private String extractUserIdFromToken(String authHeader) throws FirebaseAuthException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String token = authHeader.substring(7);
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return decodedToken.getUid();
    }

    /**
     * Create error response map
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
