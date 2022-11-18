package com.kamil.courses.service;

import com.kamil.courses.model.dto.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name="STUDENT-SERVICE", path = "/students")
public interface StudentServiceClient {
    @GetMapping("/{studentId}")
    Student getStudentById(@PathVariable Long studentId);
}
