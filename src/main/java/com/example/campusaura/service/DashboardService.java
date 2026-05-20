package com.example.campusaura.service;

import com.example.campusaura.dto.DashboardStatsDTO;
import com.example.campusaura.dto.EventResponseDTO;
import com.example.campusaura.dto.TopCoordinatorDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private EventService eventService;

    @Autowired
    private ProductService productService;

    public DashboardStatsDTO getDashboardStats() throws ExecutionException, InterruptedException {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // ── Core counts ──────────────────────────────────────────────────
        long totalEvents   = getCollectionCount("events");
        long activeUsers   = getCollectionCount("users");
        long totalProducts = getCollectionCount("products");
        long productsSold  = productService.getSoldProductsCount();

        stats.setTotalEvents(totalEvents);
        stats.setActiveUsers(activeUsers);
        stats.setTotalProducts(totalProducts);
        stats.setProductsSold(productsSold);

        // ── Published / Draft event counts ───────────────────────────────
        List<QueryDocumentSnapshot> allEventDocs =
                firestore.collection("events").get().get().getDocuments();

        long publishedCount = allEventDocs.stream()
                .filter(d -> "PUBLISHED".equalsIgnoreCase((String) d.getData().get("status")))
                .count();
        long draftCount = allEventDocs.stream()
                .filter(d -> "DRAFT".equalsIgnoreCase((String) d.getData().get("status")))
                .count();

        stats.setPublishedEvents(publishedCount);
        stats.setDraftEvents(draftCount);

        // ── Recent events (last 5) ────────────────────────────────────────
        List<EventResponseDTO> recentEvents = eventService.getRecentEvents(5);
        stats.setRecentEvents(recentEvents);

        // ── Top coordinators by event count ──────────────────────────────
        Map<String, Long> eventCountByCoordinator = new HashMap<>();
        for (QueryDocumentSnapshot doc : allEventDocs) {
            String coordId = (String) doc.getData().get("coordinatorId");
            if (coordId != null && !coordId.isEmpty()) {
                eventCountByCoordinator.merge(coordId, 1L, Long::sum);
            }
        }

        // Sort by event count descending, take top 5
        List<Map.Entry<String, Long>> sorted = eventCountByCoordinator.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<TopCoordinatorDTO> topCoordinators = new ArrayList<>();
        for (Map.Entry<String, Long> entry : sorted) {
            String coordId = entry.getKey();
            String[] info  = getCoordinatorInfo(coordId); // [name, email, degree]
            TopCoordinatorDTO dto = new TopCoordinatorDTO(
                coordId, info[0], info[1], info[2], entry.getValue().intValue());
            topCoordinators.add(dto);
        }
        stats.setTopCoordinators(topCoordinators);

        return stats;
    }

    /** Count all documents in a Firestore collection */
    private long getCollectionCount(String collectionName) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
        return future.get().getDocuments().size();
    }

    /** Fetch coordinator name, email and degree from the coordinators collection.
     *  Returns String[3]: [name, email, degree] — any field may be null if missing. */
    private String[] getCoordinatorInfo(String coordinatorId) {
        try {
            DocumentSnapshot doc = firestore.collection("coordinators")
                    .document(coordinatorId).get().get();
            if (doc.exists()) {
                Map<String, Object> data = doc.getData();

                // Resolve display name
                String name = (String) data.get("fullName");
                if (name == null || name.isEmpty()) {
                    String first = (String) data.get("firstName");
                    String last  = (String) data.get("lastName");
                    if (first != null || last != null) {
                        name = ((first != null ? first : "") + " " + (last != null ? last : "")).trim();
                    }
                }
                if (name == null || name.isEmpty()) name = (String) data.get("name");

                // Email
                String email = (String) data.get("email");

                // Degree
                String degree = (String) data.get("degree");
                if (degree == null || degree.isEmpty()) degree = (String) data.get("degreeProgramme");
                if (degree == null || degree.isEmpty()) degree = (String) data.get("department");

                // Final name fallback
                if (name == null || name.isEmpty()) name = email;
                if (name == null || name.isEmpty()) name = "Coordinator " + coordinatorId.substring(0, Math.min(6, coordinatorId.length()));

                return new String[]{ name, email, degree };
            }
        } catch (Exception ignored) {}
        return new String[]{
            "Coordinator " + coordinatorId.substring(0, Math.min(6, coordinatorId.length())),
            null, null
        };
    }
}
