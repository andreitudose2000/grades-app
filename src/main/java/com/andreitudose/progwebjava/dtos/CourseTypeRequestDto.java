package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.CourseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseTypeRequestDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    private boolean isConsideredForGradeAverage;

    public CourseType toCourseType(CourseType course) {
        course.setName(this.getName());


        return course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConsideredForGradeAverage() {
        return isConsideredForGradeAverage;
    }

    public void setConsideredForGradeAverage(boolean consideredForGradeAverage) {
        isConsideredForGradeAverage = consideredForGradeAverage;
    }
}
