package de.htwg.course.dto.courses.get;

import de.htwg.course.model.CourseMaterial;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CourseMaterialGetDTO {
    public Long id;
    public List<String> files;

    public CourseMaterialGetDTO(CourseMaterial courseMaterial) {

        if (courseMaterial != null) {
            this.id = courseMaterial.id;
            if (!Hibernate.isInitialized(courseMaterial.files)) {
                Hibernate.initialize(courseMaterial.files);
            }
            this.files = courseMaterial.files != null
                    ? courseMaterial.files.stream().collect(Collectors.toList())
                    : new ArrayList<>();
        }
    }
}

