package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.CourseType;

import java.math.BigDecimal;

public class CourseTypeResponseDto {
    private Integer id;
    private String name;
    private boolean isConsideredForGradeAverage;

    public CourseTypeResponseDto fromCourseType(CourseType courseType) {

        setId(courseType.getId());
        setName(courseType.getName());
        setConsideredForGradeAverage(courseType.isConsideredForGradeAverage());

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

    public boolean isConsideredForGradeAverage() {
        return isConsideredForGradeAverage;
    }

    public void setConsideredForGradeAverage(boolean consideredForGradeAverage) {
        isConsideredForGradeAverage = consideredForGradeAverage;
    }
}
