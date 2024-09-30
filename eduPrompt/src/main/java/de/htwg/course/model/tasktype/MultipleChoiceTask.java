package de.htwg.course.model.tasktype;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.htwg.course.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.Map;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends Task {
    private static final Gson gson = new Gson();

    @Column(length = 1024)
    private String optionsJson; // Stores the JSON string of options


    public Map<String, String> getOptions() {
        return gson.fromJson(optionsJson, Map.class);
    }

    public void setOptions(Map<String, String> options) {
        this.optionsJson = gson.toJson(options);
    }
}