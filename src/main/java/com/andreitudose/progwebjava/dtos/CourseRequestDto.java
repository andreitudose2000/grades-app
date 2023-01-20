package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Course;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CourseRequestDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    private Integer courseTypeId;

    @Min(value = 1)
    private Integer numberOfCredits;

    @Min(0)
    @Max(10)
    private Integer grade;

    public Course toCourse(Course course) {
        course.setName(this.getName());
        course.setNumberOfCredits(this.getNumberOfCredits());
        course.setGrade(this.getGrade());

        return course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCourseTypeId() {
        return courseTypeId;
    }

    public void setCourseTypeId(Integer courseTypeId) {
        this.courseTypeId = courseTypeId;
    }

    public Integer getNumberOfCredits() {
        return numberOfCredits;
    }

    public void setNumberOfCredits(Integer numberOfCredits) {
        this.numberOfCredits = numberOfCredits;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
