package com.example.campusaura.controller;

import com.example.campusaura.dto.*;
import com.example.campusaura.model.User;
import com.example.campusaura.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CoordinatorService coordinatorService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionService transactionService;

    // ==================== DASHBOARD SECTION ====================

    /**
     * Get dashboard statistics
     * GET /api/admin/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== COORDINATOR MANAGEMENT SECTION ====================

    /**
     * Register a new coordinator
     * POST /api/admin/coordinators
     */
    @PostMapping("/coordinators")
    public ResponseEntity<CoordinatorResponseDTO> registerCoordinator(@RequestBody CoordinatorRequestDTO request) {
        try {
            CoordinatorResponseDTO coordinator = coordinatorService.registerCoordinator(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(coordinator);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all coordinators
     * GET /api/admin/coordinators
     */
    @GetMapping("/coordinators")
    public ResponseEntity<List<CoordinatorResponseDTO>> getAllCoordinators() {
        try {
            List<CoordinatorResponseDTO> coordinators = coordinatorService.getAllCoordinators();
            return ResponseEntity.ok(coordinators);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get coordinator by ID
     * GET /api/admin/coordinators/{id}
     */
    @GetMapping("/coordinators/{id}")
    public ResponseEntity<CoordinatorResponseDTO> getCoordinatorById(@PathVariable String id) {
        try {
            CoordinatorResponseDTO coordinator = coordinatorService.getCoordinatorById(id);
            return ResponseEntity.ok(coordinator);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update coordinator status (active/inactive)
     * PATCH /api/admin/coordinators/{id}/status
     */
    @PatchMapping("/coordinators/{id}/status")
    public ResponseEntity<CoordinatorResponseDTO> updateCoordinatorStatus(
            @PathVariable String id, 
            @RequestBody Map<String, Boolean> statusUpdate) {
        try {
            boolean active = statusUpdate.get("active");
            CoordinatorResponseDTO coordinator = coordinatorService.updateCoordinatorStatus(id, active);
            return ResponseEntity.ok(coordinator);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete coordinator
     * DELETE /api/admin/coordinators/{id}
     */
    @DeleteMapping("/coordinators/{id}")
    public ResponseEntity<Map<String, String>> deleteCoordinator(@PathVariable String id) {
        try {
            coordinatorService.deleteCoordinator(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Coordinator deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get degree programmes for dropdown
     * GET /api/admin/coordinators/degree-programmes
     */
    @GetMapping("/coordinators/degree-programmes")
    public ResponseEntity<List<String>> getDegreeProgrammes() {
        List<String> programmes = List.of(
            "Animal Production and Food Technology",
            "Export Agriculture",
            "Aquatic Resources Technology",
            "Tea Technology and Value Addition",
            "Computer Science and Technology",
            "Industrial Information Technology",
            "Science & Technology",
            "Mineral Resources and Technology",
            "Entrepreneurship & Management Studies",
            "Hospitality, Tourism & Events Management",
            "Human Resource Development",
            "English Language & Applied Linguistics",
            "Engineering Technology",
            "Biosystems Technology Honours",
            "Information and Communication Technology Honours"
        );
        return ResponseEntity.ok(programmes);
    }

    // ==================== EVENT MANAGEMENT SECTION ====================

    /**
     * Get all events
     * GET /api/admin/events
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        try {
            List<EventResponseDTO> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event by ID
     * GET /api/admin/events/{id}
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable String id) {
        try {
            EventResponseDTO event = eventService.getEventByIdDTO(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete event
     * DELETE /api/admin/events/{id}
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Map<String, String>> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Event deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Filter events by category
     * GET /api/admin/events/filter?category={category}
     */
    @GetMapping("/events/filter")
    public ResponseEntity<List<EventResponseDTO>> filterEventsByCategory(@RequestParam String category) {
        try {
            List<EventResponseDTO> events = eventService.getEventsByCategory(category);
            return ResponseEntity.ok(events);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== USER MANAGEMENT SECTION ====================

    /**
     * Get all users
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get university students
     * GET /api/admin/users/university-students
     */
    @GetMapping("/users/university-students")
    public ResponseEntity<List<UserResponseDTO>> getUniversityStudents() {
        try {
            List<UserResponseDTO> users = userService.getUsersByType(User.UserType.UNIVERSITY_STUDENT);
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get external users
     * GET /api/admin/users/external-users
     */
    @GetMapping("/users/external-users")
    public ResponseEntity<List<UserResponseDTO>> getExternalUsers() {
        try {
            List<UserResponseDTO> users = userService.getUsersByType(User.UserType.EXTERNAL_USER);
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get pending verification users
     * GET /api/admin/users/pending-verification
     */
    @GetMapping("/users/pending-verification")
    public ResponseEntity<List<UserResponseDTO>> getPendingVerificationUsers() {
        try {
            List<UserResponseDTO> users = userService.getPendingVerificationUsers();
            return ResponseEntity.ok(users);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user statistics
     * GET /api/admin/users/stats
     */
    @GetMapping("/users/stats")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        try {
            UserStatsDTO stats = userService.getUserStats();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update user status (active/inactive)
     * PATCH /api/admin/users/{id}/status
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable String id, 
            @RequestBody Map<String, Boolean> statusUpdate) {
        try {
            boolean active = statusUpdate.get("active");
            UserResponseDTO user = userService.updateUserStatus(id, active);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verify student (approve/reject)
     * PATCH /api/admin/users/{id}/verify
     */
    @PatchMapping("/users/{id}/verify")
    public ResponseEntity<UserResponseDTO> verifyStudent(
            @PathVariable String id, 
            @RequestBody Map<String, String> verificationUpdate) {
        try {
            String statusStr = verificationUpdate.get("status");
            User.VerificationStatus status = User.VerificationStatus.valueOf(statusStr);
            UserResponseDTO user = userService.verifyStudent(id, status);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete user
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== PRODUCT MANAGEMENT SECTION ====================

    /**
     * Get all products
     * GET /api/admin/products
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        try {
            List<ProductResponseDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get product by ID
     * GET /api/admin/products/{id}
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        try {
            ProductResponseDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete product
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== PAYMENT SECTION ====================

    /**
     * Get payment statistics
     * GET /api/admin/payments/stats
     */
    @GetMapping("/payments/stats")
    public ResponseEntity<PaymentStatsDTO> getPaymentStats() {
        try {
            PaymentStatsDTO stats = transactionService.getPaymentStats();
            return ResponseEntity.ok(stats);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all transactions
     * GET /api/admin/payments/transactions
     */
    @GetMapping("/payments/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        try {
            List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get recent transactions
     * GET /api/admin/payments/transactions/recent?limit={limit}
     */
    @GetMapping("/payments/transactions/recent")
    public ResponseEntity<List<TransactionResponseDTO>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<TransactionResponseDTO> transactions = transactionService.getRecentTransactions(limit);
            return ResponseEntity.ok(transactions);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
