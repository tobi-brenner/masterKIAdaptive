package de.htwg.studentlevel;

import com.fasterxml.jackson.annotation.JsonCreator;

public record StudentLevel(Evaluation evaluation, String message) {

    @JsonCreator
    public StudentLevel{
    }
}
