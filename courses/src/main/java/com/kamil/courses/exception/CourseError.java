package com.kamil.courses.exception;

public enum CourseError {

    COURSE_NOT_FOUND("Course does not exists"),
    COURSE_IS_NOT_ACTIVE("Course is not active"),
    STUDENT_IS_NOT_ACTIVE("Student is not active"),
    STUDENT_ALREADY_ENROLLED("Student already enrolled"),
    COURSE_CODE_ALREADY_EXISTS("Course already exists"),
    COURSE_START_DATE_IS_AFTER_END_DATE("Course start date is after end date"),
    COURSE_PARTICIPANTS_LIMIT_IS_EXCEEDED("Course participants limit is exceeded"),
    COURSE_CAN_NOT_SET_FULL_STATUS("Course can not set full status"),
    COURSE_CAN_NOT_SET_ACTIVE_STATUS("Course can not set active status"),
    STUDENT_CAN_NOT_BE_ENROLLED("Student can not be enrolled");

    private String message;

    CourseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}