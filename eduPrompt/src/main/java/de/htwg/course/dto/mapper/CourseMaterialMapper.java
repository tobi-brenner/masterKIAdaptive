package de.htwg.course.dto.mapper;

import de.htwg.course.dto.courses.get.CourseMaterialGetDTO;
import de.htwg.course.model.CourseMaterial;

public class CourseMaterialMapper {
    public static CourseMaterialGetDTO toDTO(CourseMaterial courseMaterial) {
        return new CourseMaterialGetDTO(courseMaterial);
    }

    public static CourseMaterial toEntity(CourseMaterialGetDTO dto) {
        CourseMaterial courseMaterial = new CourseMaterial();
        courseMaterial.id = dto.id;
        courseMaterial.files = dto.files;
        return courseMaterial;
    }
}
