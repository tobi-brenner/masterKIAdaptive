package de.htwg.course.model.tasktype;

import de.htwg.course.model.Task;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SHORT_ANSWER")
public class ShortAnswerTask extends Task {
}
