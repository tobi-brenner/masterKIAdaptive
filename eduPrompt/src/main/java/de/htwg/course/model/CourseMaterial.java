package de.htwg.course.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class CourseMaterial extends PanacheEntity {

    public String fullScript; // Path to the full script PDF
    public String summaryScript; // Path to the summary script PDF

    @ElementCollection
    @CollectionTable(name = "course_material_files", joinColumns = @JoinColumn(name = "course_material_id"))
    @Column(name = "file_path")
    public List<String> files = new ArrayList<>();

    @OneToOne
    @JsonBackReference(value = "course-material")
    public Course course;


    public CourseMaterial() {
        this.files = new ArrayList<>();
    }

}
