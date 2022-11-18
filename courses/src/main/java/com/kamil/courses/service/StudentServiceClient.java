package com.kamil.courses.service;

import com.kamil.courses.model.dto.StudentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name="STUDENT-SERVICE", path = "/students")
public interface StudentServiceClient {
    @GetMapping("/{studentId}")
    StudentDto getStudentById(@PathVariable Long studentId);

    @PostMapping("/emails")
    List<StudentDto> getStudentsByEmails(@RequestBody List <String> emails);
}
