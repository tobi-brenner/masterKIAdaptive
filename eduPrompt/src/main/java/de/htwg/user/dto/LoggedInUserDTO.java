package de.htwg.user.dto;

import de.htwg.user.model.User;

public class LoggedInUserDTO {
    public Long id;
    public String email;
    public String password;
    public String studentNumber;
    public String firstName;
    public String lastName;
    public String role;

    public LoggedInUserDTO() {
    }

    public LoggedInUserDTO(User user) {
        this.id = user.id;
        this.email = user.email;
        this.password = user.password;
        this.studentNumber = user.studentNumber;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.role = user.role;
    }
}
