package com.kamil.students.repository;

import com.kamil.students.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Ta adnotacja jest opcjonalna
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    List<Student> findAllByStatus(Student.Status status);
}