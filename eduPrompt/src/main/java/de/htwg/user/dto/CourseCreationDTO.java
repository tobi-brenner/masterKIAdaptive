package de.htwg.user.dto;

import java.time.temporal.ChronoUnit;

public class CourseCreationDTO {
    public String subject;
    public String description;
    public String language;
    public ChronoUnit periodUnit;
    public int periodLength;

    public Long profUserId;

    @Override
    public String toString() {
        return "CourseCreationDTO{" +
                "subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", periodUnit=" + periodUnit +
                ", periodLength=" + periodLength +
                ", profUserId=" + profUserId +
                '}';
    }
}
