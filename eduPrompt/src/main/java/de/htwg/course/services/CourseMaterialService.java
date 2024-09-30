package de.htwg.course.services;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import de.htwg.course.model.Course;
import de.htwg.course.model.CourseMaterial;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class CourseMaterialService {

    @Inject
    EntityManager em;

    public void addFilesToCourseMaterial(Long courseId, List<String> filePaths) {
        Course course = em.find(Course.class, courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found for ID: " + courseId);
        }
        CourseMaterial courseMaterial = course.courseMaterial;

        if (courseMaterial == null) {
            courseMaterial = new CourseMaterial();
            courseMaterial.course = course;
            course.courseMaterial = courseMaterial;
            em.persist(courseMaterial);
        }

        for (String filePath : filePaths) {
            courseMaterial.files.add(filePath);
        }
        Log.info("--------------");
        Log.info(courseMaterial.files);
        Log.info("--------------");

        em.merge(courseMaterial);
    }
}
