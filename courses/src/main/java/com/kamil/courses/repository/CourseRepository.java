package com.kamil.courses.repository;

import com.kamil.courses.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findAllByStatus(Course.Status status);
    boolean existsByCode(String code);
}
