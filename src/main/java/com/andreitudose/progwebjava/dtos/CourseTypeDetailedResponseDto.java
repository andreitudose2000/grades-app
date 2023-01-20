package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.CourseType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseTypeDetailedResponseDto {
    private Integer id;
    private String name;
    private boolean isConsideredForGradeAverage;
    private Set<CourseResponseDto> courses = new HashSet<>();

    public CourseTypeDetailedResponseDto fromCourseType(CourseType courseType) {

        setId(courseType.getId());
        setName(courseType.getName());
        setConsideredForGradeAverage(courseType.isConsideredForGradeAverage());
        setCourses(
                courseType.getCourses().stream()
                        .map(x -> new CourseResponseDto().fromCourse(x))
                        .collect(Collectors.toSet())
        );
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

    public Set<CourseResponseDto> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseResponseDto> courses) {
        this.courses = courses;
    }
}
