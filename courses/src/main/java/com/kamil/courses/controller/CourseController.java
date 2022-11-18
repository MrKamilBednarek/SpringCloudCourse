package com.kamil.courses.controller;

import com.kamil.courses.model.Course;
import com.kamil.courses.model.dto.StudentDto;
import com.kamil.courses.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;


    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getCourses(@RequestParam (required = false) Course.Status status){

        return courseService.getCourses(status);
    }
    @PostMapping
    public Course addCourse(@Valid @RequestBody Course course){
        return courseService.addCourse(course);
    }

    @GetMapping("/{code}")
    public Course getCourse(@PathVariable String code){
        return courseService.getCourse(code);
    }
    @DeleteMapping("/{code}")
    public void deleteCourse(@PathVariable String code) {
        courseService.deleteCourse(code);
    }

    @PutMapping("/{code}")
    public Course putCourse(@PathVariable String code, @Valid @RequestBody Course course) {
        return courseService.putCourse(code, course);
    }

    @PatchMapping("/{code}")
    public Course patchCourse(@PathVariable String code, @RequestBody Course course) {
        return courseService.patchCourse(code, course);
    }
    @PostMapping("/{courseCode}/student/{studentId}")
    public ResponseEntity<?> courseEnrollment(@PathVariable String courseCode, @PathVariable Long studentId){
        courseService.courseEnrollment(courseCode,studentId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{code}/members")
    public List<StudentDto> getCourseMembers(@PathVariable String code){
    return courseService.getCourseMembers(code);
    }
}
