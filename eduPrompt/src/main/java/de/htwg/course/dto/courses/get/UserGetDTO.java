package de.htwg.course.dto.courses.get;

import de.htwg.user.model.User;

public class UserGetDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public String name;
    public String email;

    public UserGetDTO(User user) {
        this.id = user.id;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.name = user.firstName + " " + user.lastName;
    }
}



