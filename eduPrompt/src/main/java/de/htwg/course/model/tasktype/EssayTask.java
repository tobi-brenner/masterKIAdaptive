package de.htwg.course.model.tasktype;


import de.htwg.course.model.Task;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESSAY")
public class EssayTask extends Task {
}
