package com.example.campusaura.dto;

public class StudentRegistrationDTO {
    private String uid;
    private String email;
    private String name;
    private String degreeProgram;
    private String studentIdImageUrl;

    public StudentRegistrationDTO() {
    }

    public StudentRegistrationDTO(String uid, String email, String name, String degreeProgram, String studentIdImageUrl) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.degreeProgram = degreeProgram;
        this.studentIdImageUrl = studentIdImageUrl;
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
}
