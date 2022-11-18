package com.kamil.courses.service;

import com.kamil.courses.exception.CourseError;
import com.kamil.courses.exception.CourseException;
import com.kamil.courses.model.Course;
import com.kamil.courses.model.CourseMember;
import com.kamil.courses.model.dto.Student;
import com.kamil.courses.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final StudentServiceClient studentServiceClient;

    public CourseServiceImpl(CourseRepository courseRepository, StudentServiceClient studentServiceClient) {
        this.courseRepository = courseRepository;
        this.studentServiceClient = studentServiceClient;
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

        validateCourseStatus(course);
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


    public void courseEnrollment(String courseCode,Long studentId) {
        Course course = getCourse(courseCode);
        validateCourseStatus(course);
        Student student=studentServiceClient.getStudentById(studentId);
        validateStudentBeforeCourseEnrollment(course, student);
        course.incrementParticipantsNumber();
        course.getCourseMember().add(new CourseMember(student.getEmail()));
        courseRepository.save(course);
    }

    private static void validateStudentBeforeCourseEnrollment(Course course, Student student) {
        if(!Student.Status.ACTIVE.equals(student.getStatus())){
            throw new CourseException(CourseError.STUDENT_IS_NOT_ACTIVE);
        }

        if(course.getCourseMember().stream()
                .anyMatch(member-> student.getEmail().equals(member.getEmail()))){
            throw new CourseException(CourseError.STUDENT_ALREADY_ENROLLED);
        }
    }

    private static void validateCourseStatus(Course course) {
        if(!Course.Status.ACTIVE.equals(course.getStatus())){
            throw new CourseException(CourseError.COURSE_IS_NOT_ACTIVE);
        }
    }
}
