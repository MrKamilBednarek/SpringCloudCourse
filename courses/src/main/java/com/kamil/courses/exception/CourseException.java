package com.kamil.courses.exception;

public class CourseException extends RuntimeException {

    private CourseError courseError;

    public CourseException(CourseError studentError) {
        this.courseError = studentError;
    }

    public CourseError getCourseError() {
        return courseError;
    }
}