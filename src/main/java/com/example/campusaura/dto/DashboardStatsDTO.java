package com.example.campusaura.dto;

import com.example.campusaura.dto.EventResponseDTO;
import java.util.List;

public class DashboardStatsDTO {
    private long totalEvents;
    private long totalUsers;
    private long totalProducts;
    private long productsSold;
    private List<EventResponseDTO> recentEvents;

    // Constructors
    public DashboardStatsDTO() {}

    public DashboardStatsDTO(long totalEvents, long totalUsers, long totalProducts, 
                             long productsSold, List<EventResponseDTO> recentEvents) {
        this.totalEvents = totalEvents;
        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.productsSold = productsSold;
        this.recentEvents = recentEvents;
    }

    // Getters and Setters
    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(long productsSold) {
        this.productsSold = productsSold;
    }

    public List<EventResponseDTO> getRecentEvents() {
        return recentEvents;
    }

    public void setRecentEvents(List<EventResponseDTO> recentEvents) {
        this.recentEvents = recentEvents;
    }
}
