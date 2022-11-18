package com.kamil.courses.model;



import com.kamil.courses.exception.CourseError;
import com.kamil.courses.exception.CourseException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
public class Course {

    @Id
    private String code;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Future
    private LocalDateTime startDate;
    @NotNull
    @Future
    private LocalDateTime endDate;
    @Min(0)
    private Long participantsLimit;
    @NotNull
    @Min(0)
    private Long participantsNumber;
    @NotNull
    private Status status;
    private List<CourseMember> courseMember = new ArrayList<>();
    public enum Status {
        ACTIVE,
        INACTIVE,
        FULL}
    private void validateCourseDate(){
        if(startDate.isAfter(endDate)){
            throw new CourseException(CourseError.COURSE_START_DATE_IS_AFTER_END_DATE);
        }
    };
    private void validateParticipantsLimit(){
        if(participantsNumber > participantsLimit){
            throw new CourseException(CourseError.COURSE_PARTICIPANTS_LIMIT_IS_EXCEEDED);
        }
    }
    private void validateStatus(){
        if(Status.FULL.equals(status)&& !(participantsNumber==(participantsLimit))){
            throw new CourseException(CourseError.COURSE_CAN_NOT_SET_FULL_STATUS);
        }
        if(Status.ACTIVE.equals(status)&&(participantsNumber==(participantsLimit))){
            throw new CourseException(CourseError.COURSE_CAN_NOT_SET_ACTIVE_STATUS);
        }
    }
    public void validateCourse(){
        validateCourseDate();
        validateStatus();
        validateParticipantsLimit();
    }
    public void incrementParticipantsNumber(){
        participantsNumber++;
        if(participantsNumber==participantsLimit){
            setStatus(Course.Status.FULL);
        }
    }
}