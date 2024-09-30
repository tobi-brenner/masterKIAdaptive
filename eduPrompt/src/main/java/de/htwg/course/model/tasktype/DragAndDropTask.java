package de.htwg.course.model.tasktype;

import com.google.gson.Gson;
import de.htwg.course.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Map;

@Entity
@DiscriminatorValue("DRAG_AND_DROP")
public class DragAndDropTask extends Task {
    private static final Gson gson = new Gson();

    @Column(length = 1024)
    private String optionsJson;


    public Map<String, String> getOptions() {
        return gson.fromJson(optionsJson, Map.class);
    }

    public void setOptions(Map<String, String> options) {
        this.optionsJson = gson.toJson(options);
    }

}
