package com.kamil.courses.service;

import com.kamil.courses.model.Course;
import com.kamil.courses.model.dto.StudentDto;

import java.util.List;

public interface CourseService {
    List<Course> getCourses(Course.Status status);
    Course getCourse(String code);
    Course addCourse(Course course);

    void deleteCourse(String code);

    Course putCourse(String code, Course course);

    Course patchCourse(String code, Course course);

    void courseEnrollment(String courseCode,Long studentId);

    List<StudentDto> getCourseMembers (String courseCode);
}
