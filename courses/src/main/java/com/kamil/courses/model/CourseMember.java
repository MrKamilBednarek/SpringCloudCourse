package com.kamil.courses.model;

import lombok.Getter;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
public class CourseMember {
    @NotNull
    private LocalDateTime enrollmentDate;
    @NotNull
    private String email;

    public CourseMember(String email) {
        this.enrollmentDate = LocalDateTime.now();
        this.email = email;
    }
}
