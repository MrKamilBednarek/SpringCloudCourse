package com.kamil.courses.service;

import com.kamil.courses.exception.CourseError;
import com.kamil.courses.exception.CourseException;
import com.kamil.courses.model.Course;
import com.kamil.courses.model.CourseMember;
import com.kamil.courses.model.dto.NotificationInfoDto;
import com.kamil.courses.model.dto.StudentDto;
import com.kamil.courses.repository.CourseRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{

    public static final String EXCHANGE_ENROLL_FINISH = "enroll_finish";
    private final CourseRepository courseRepository;
    private final StudentServiceClient studentServiceClient;
    private final RabbitTemplate rabbitTemplate;
    public CourseServiceImpl(CourseRepository courseRepository, StudentServiceClient studentServiceClient, RabbitTemplate rabbitTemplate) {
        this.courseRepository = courseRepository;
        this.studentServiceClient = studentServiceClient;
        this.rabbitTemplate = rabbitTemplate;
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

        //validateCourseStatus(course);
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
        StudentDto studentDto =studentServiceClient.getStudentById(studentId);
        validateStudentBeforeCourseEnrollment(course, studentDto);
        course.incrementParticipantsNumber();
        course.getCourseMembers().add(new CourseMember(studentDto.getEmail()));
        courseRepository.save(course);
    }

    public List<StudentDto> getCourseMembers(String courseCode) {
        Course course = getCourse(courseCode);
        List<@NotNull String> emailsMembers = getCourseMembersEmails(course);
        return studentServiceClient.getStudentsByEmails(emailsMembers);

    }

    private static List<@NotNull String> getCourseMembersEmails(Course course) {
        List<@NotNull String> emailsMembers = course.getCourseMembers().stream()
                .map(CourseMember::getEmail).collect(Collectors.toList());
        return emailsMembers;
    }

    public void courseFinishEnroll(String courseCode) {
        Course course = getCourse(courseCode);
        if(Course.Status.INACTIVE.equals(course.getStatus())){
            throw new CourseException(CourseError.COURSE_IS_INACTIVE);
        }
        course.setStatus(Course.Status.INACTIVE);
        courseRepository.save(course);
        sendMessageToRabbitMq(course);
    }

    private void sendMessageToRabbitMq(Course course) {
        NotificationInfoDto notificationInfoDto = createNotificationInfo(course);
        rabbitTemplate.convertAndSend(EXCHANGE_ENROLL_FINISH,notificationInfoDto);
    }

    private static NotificationInfoDto createNotificationInfo(Course course) {
        List<@NotNull String> emailsMembers = getCourseMembersEmails(course);
        NotificationInfoDto notificationInfoDto = NotificationInfoDto.builder()
                .courseCode(course.getCode())
                .courseName(course.getName())
                .courseDescription(course.getDescription())
                .courseStartDate(course.getStartDate())
                .courseEndDate(course.getEndDate())
                .emails(emailsMembers)
                .build();
        return notificationInfoDto;
    }


    private static void validateStudentBeforeCourseEnrollment(Course course, StudentDto studentDto) {
        if(!StudentDto.Status.ACTIVE.equals(studentDto.getStatus())){
            throw new CourseException(CourseError.STUDENT_IS_NOT_ACTIVE);
        }

        if(course.getCourseMembers().stream()
                .anyMatch(member-> studentDto.getEmail().equals(member.getEmail()))){
            throw new CourseException(CourseError.STUDENT_ALREADY_ENROLLED);
        }
    }

    private static void validateCourseStatus(Course course) {
        if(!Course.Status.ACTIVE.equals(course.getStatus())){
            throw new CourseException(CourseError.COURSE_IS_NOT_ACTIVE);
        }
    }
}
