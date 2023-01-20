package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Semester;
import com.andreitudose.progwebjava.model.YearOfStudy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SemesterDetailedResponseDto {
    private Integer id;
    private Integer number;
    private Set<CourseResponseDto> courses = new HashSet<>();
    public SemesterDetailedResponseDto fromSemester(Semester semester) {

        setId(semester.getId());
        setNumber(semester.getNumber());
        setCourses(
                semester.getCourses().stream()
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Set<CourseResponseDto> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseResponseDto> courses) {
        this.courses = courses;
    }
}
