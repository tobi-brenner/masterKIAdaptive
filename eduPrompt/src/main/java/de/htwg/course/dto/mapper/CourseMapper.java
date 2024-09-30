package de.htwg.course.dto.mapper;

import de.htwg.course.dto.courses.get.CourseGetDTO;
import de.htwg.course.model.Course;

import java.util.stream.Collectors;

public class CourseMapper {
    public static CourseGetDTO toDTO(Course course) {
        return new CourseGetDTO(course);

    }

    public static Course toEntity(CourseGetDTO courseGetDTO) {
        Course course = new Course();
        course.id = courseGetDTO.id;
        course.subject = courseGetDTO.subject;
        course.description = courseGetDTO.description;
        course.topics = courseGetDTO.topics != null ? courseGetDTO.topics.stream().map(TopicMapper::toEntity).collect(Collectors.toList()) : null;
        course.courseMaterial = courseGetDTO.courseMaterial != null ? CourseMaterialMapper.toEntity(courseGetDTO.courseMaterial) : null;
        // Set other properties if needed
        return course;
    }
}

