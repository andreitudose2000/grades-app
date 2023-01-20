package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Course;
import com.andreitudose.progwebjava.model.Semester;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class CourseResponseDto {
    private Integer id;

    private String name;

    private Integer numberOfCredits;

    private BigDecimal grade;

    public CourseResponseDto fromCourse(Course course) {

        setId(course.getId());
        setName(course.getName());
        setGrade(course.getGrade());
        setNumberOfCredits(course.getNumberOfCredits());

        return this;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfCredits() {
        return numberOfCredits;
    }

    public void setNumberOfCredits(Integer numberOfCredits) {
        this.numberOfCredits = numberOfCredits;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }
}
