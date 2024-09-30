//package de.htwg.course.dto;
//
//import de.htwg.course.model.Course;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class CourseDTO {
//    public Long id;
//    public String subject;
//    public String description;
//    public List<TopicDTO> topics;
//
//    public CourseDTO(Course course) {
//        this.id = course.id;
//        this.subject = course.subject;
//        this.description = course.description;
//        this.topics = course.topics.stream().map(TopicDTO::new).collect(Collectors.toList());
//    }
//}
