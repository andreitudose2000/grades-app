package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Course;

public class CourseResponseDto {
    private Integer id;

    private String name;

    private Integer numberOfCredits;

    private Integer grade;

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

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
