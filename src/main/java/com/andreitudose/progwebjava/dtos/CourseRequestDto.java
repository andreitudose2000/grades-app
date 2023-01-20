package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Course;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CourseRequestDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @Min(value = 1)
    private Integer numberOfCredits;

    @Min(0)
    @Max(10)
    private BigDecimal grade;

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
