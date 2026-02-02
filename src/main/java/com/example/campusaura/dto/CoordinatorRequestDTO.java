package com.example.campusaura.dto;

public class CoordinatorRequestDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String degreeProgramme;

    // Constructors
    public CoordinatorRequestDTO() {}

    public CoordinatorRequestDTO(String firstName, String lastName, String phoneNumber, 
                                 String email, String degreeProgramme) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.degreeProgramme = degreeProgramme;
    }

    // Getters and Setters
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDegreeProgramme() {
        return degreeProgramme;
    }

    public void setDegreeProgramme(String degreeProgramme) {
        this.degreeProgramme = degreeProgramme;
    }
}
