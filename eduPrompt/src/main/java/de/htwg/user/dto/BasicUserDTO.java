package de.htwg.user.dto;

import de.htwg.user.model.User;

public class BasicUserDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public String studentNumber;
    public String email;

    // Constructors, getters and setters
    public BasicUserDTO() {
    }

    public BasicUserDTO(User user) {
        this.id = user.id;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.studentNumber = user.studentNumber;
        this.email = user.email;
    }

    @Override
    public String toString() {
        return "BasicUserDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
