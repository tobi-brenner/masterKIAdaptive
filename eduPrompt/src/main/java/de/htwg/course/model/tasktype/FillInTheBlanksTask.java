package de.htwg.course.model.tasktype;

import de.htwg.course.model.Task;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FILL_IN_THE_BLANKS")
public class FillInTheBlanksTask extends Task {
}
