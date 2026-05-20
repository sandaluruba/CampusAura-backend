package com.example.campusaura.dto;

public class TopCoordinatorDTO {
    private String id;
    private String name;
    private String email;
    private String degree;
    private int eventCount;

    public TopCoordinatorDTO() {}

    public TopCoordinatorDTO(String id, String name, int eventCount) {
        this.id         = id;
        this.name       = name;
        this.eventCount = eventCount;
    }

    public TopCoordinatorDTO(String id, String name, String email, String degree, int eventCount) {
        this.id         = id;
        this.name       = name;
        this.email      = email;
        this.degree     = degree;
        this.eventCount = eventCount;
    }

    public String getId()       { return id; }
    public void   setId(String id) { this.id = id; }

    public String getName()       { return name; }
    public void   setName(String name) { this.name = name; }

    public String getEmail()       { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getDegree()       { return degree; }
    public void   setDegree(String degree) { this.degree = degree; }

    public int  getEventCount()          { return eventCount; }
    public void setEventCount(int count) { this.eventCount = count; }
}
