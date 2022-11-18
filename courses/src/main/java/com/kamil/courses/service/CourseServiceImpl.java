package com.kamil.courses.service;

import com.kamil.courses.exception.CourseError;
import com.kamil.courses.exception.CourseException;
import com.kamil.courses.model.Course;
import com.kamil.courses.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getCourses(Course.Status status)
    {
        if (status != null) {
            return courseRepository.findAllByStatus(status);
        }
        return courseRepository.findAll();
    }

    @Override
    public Course getCourse(String code) {
        Course course = courseRepository.findById(code)
                .orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));

        if (!Course.Status.ACTIVE.equals(course.getStatus())) {
            throw new CourseException(CourseError.COURSE_IS_NOT_ACTIVE);
        }
        return course;
    }


    @Override
    public Course addCourse(Course course) {
        course.validateCourse();
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(String code) {
        Course course = courseRepository.findById(code)
                .orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
        course.setStatus(Course.Status.INACTIVE);
        courseRepository.save(course);
    }

    @Override
    public Course putCourse(String code, Course course) {
        course.validateCourse();
        return courseRepository.findById(code)
                .map(courseFromDb -> {
                    if (!courseFromDb.getCode().equals(course.getCode()) &&
                            courseRepository.existsByCode(course.getCode())) {
                        throw new CourseException(CourseError.COURSE_CODE_ALREADY_EXISTS);
                    }
                    courseFromDb.setCode(course.getCode());
                    courseFromDb.setDescription(course.getDescription());
                    courseFromDb.setName(course.getName());
                    courseFromDb.setStatus(course.getStatus());
                    courseFromDb.setStartDate(course.getStartDate());
                    courseFromDb.setEndDate(course.getEndDate());
                    courseFromDb.setParticipantsLimit(course.getParticipantsLimit());
                    courseFromDb.setParticipantsNumber(course.getParticipantsNumber());
                    return courseRepository.save(courseFromDb);
                }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course patchCourse(String code, Course course) {
        course.validateCourse();
        return courseRepository.findById(code)
                .map(courseFromDb -> {
                    if (!StringUtils.isEmpty(course.getDescription())) {
                        courseFromDb.setDescription(course.getDescription());
                    }
                    if (!StringUtils.isEmpty(course.getName())) {
                        courseFromDb.setName(course.getName());
                    }
                    if (!StringUtils.isEmpty(course.getStatus())) {
                        courseFromDb.setStatus(course.getStatus());
                    }
                    if (!StringUtils.isEmpty(course.getParticipantsNumber())) {
                        courseFromDb.setParticipantsNumber(course.getParticipantsNumber());
                    }
                    if (!StringUtils.isEmpty(course.getStartDate())) {
                        courseFromDb.setStartDate(course.getStartDate());
                    }
                    if (!StringUtils.isEmpty(course.getEndDate())) {
                        courseFromDb.setEndDate(course.getEndDate());
                    }
                    if (!StringUtils.isEmpty(course.getParticipantsLimit())) {
                        courseFromDb.setParticipantsLimit(course.getParticipantsLimit());
                    }
                    return courseRepository.save(courseFromDb);
                }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }
}
